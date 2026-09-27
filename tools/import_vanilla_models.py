#!/usr/bin/env python3
"""
Imports block geometry and per-face textures from the vanilla block models in a Minecraft client jar.

For every block whose shape family isn't a plain cube ("full"), each block state's vanilla model(s) are
turned into a NanoCraft shape: every element box, with its face UVs, face rotations, element rotation,
the blockstate's x/y rotation and uvlock, and multipart pieces. Identical shapes are shared between
blocks and states. The output is

    src/resources/assets/model/block/vanilla.shp    the shapes, named v/<block>_<n>
    src/resources/assets/def/block/vanilla.def      <state pattern> <shape> <texture per face>

and both are added to their index files. Lines for the imported blocks are removed from the other .def
files, so re-running the tool replaces its previous output. Blocks vanilla draws as entities (chests,
signs, banners, heads, ...) have no model geometry and keep their existing shapes, as do liquids.

Short and tall grass get tint= for the faces vanilla tints, so they take the biome's grass color.
Other faces vanilla tints are written as-is, except where the color depends on the state: redstone dust
(<texture>_{power}) and growing melon/pumpkin stems (<texture>_{age}), the pre-colored textures from
tools/tinted_textures.py.

Usage:  python3 tools/import_vanilla_models.py path/to/client.jar
"""
import collections
import glob
import itertools
import json
import math
import os
import re
import sys
import zipfile

REPO = os.path.join(os.path.dirname(os.path.abspath(__file__)), "..")
ASSETS = os.path.join(REPO, "src", "resources", "assets")
JAVA = os.path.join(REPO, "src", "org", "sutormin", "nanocraft", "data", "definitions", "block")
SKIP_FAMILIES = {"full", "liquid", "air"}
# blocks whose tinted faces use one pre-colored texture per value of a property
TINT_SUFFIX = {"redstone_wire": "_{power}", "melon_stem": "_{age}", "pumpkin_stem": "_{age}"}
# blocks whose tinted faces take the biome's grass color at runtime (def option tint=)
BIOME_TINTED = {"short_grass", "tall_grass"}
DIRS = ["down", "up", "north", "south", "west", "east"]
NORMAL = {"up": (0, 1, 0), "down": (0, -1, 0), "north": (0, 0, -1), "south": (0, 0, 1), "east": (1, 0, 0), "west": (-1, 0, 0)}
# Minecraft's corner order for each face (FaceInfo), counter-clockwise seen from outside
CORNERS = {
    "down": [(0, 0, 1), (0, 0, 0), (1, 0, 0), (1, 0, 1)],
    "up": [(0, 1, 0), (0, 1, 1), (1, 1, 1), (1, 1, 0)],
    "north": [(1, 1, 0), (1, 0, 0), (0, 0, 0), (0, 1, 0)],
    "south": [(0, 1, 1), (0, 0, 1), (1, 0, 1), (1, 1, 1)],
    "west": [(0, 1, 0), (0, 0, 0), (0, 0, 1), (0, 1, 1)],
    "east": [(1, 1, 1), (1, 0, 1), (1, 0, 0), (1, 1, 0)],
}


def default_uv(face, f, t):
    """Minecraft's default face UVs for an element from f to t (0..16)."""
    return {
        "down": [f[0], 16 - t[2], t[0], 16 - f[2]],
        "up": [f[0], f[2], t[0], t[2]],
        "north": [16 - t[0], 16 - t[1], 16 - f[0], 16 - f[1]],
        "south": [f[0], 16 - t[1], t[0], 16 - f[1]],
        "west": [f[2], 16 - t[1], t[2], 16 - f[1]],
        "east": [16 - t[2], 16 - t[1], 16 - f[2], 16 - f[1]],
    }[face]


def corner_uv(uv, rotation, i):
    """UV of corner i of a face with uv rect and rotation (Minecraft's BlockFaceUV)."""
    s = (i + rotation // 90) % 4
    return (uv[0] if s in (0, 1) else uv[2], uv[1] if s in (0, 3) else uv[3])


def rotate_axis(p, axis, deg, origin, rescale):
    a = math.radians(deg)
    c, s = math.cos(a), math.sin(a)
    x, y, z = (p[i] - origin[i] for i in range(3))
    if axis == "x": y, z = y * c - z * s, y * s + z * c
    elif axis == "y": x, z = x * c + z * s, -x * s + z * c
    else: x, y = x * c - y * s, x * s + y * c
    if rescale and abs(c) > 1e-6:
        k = 1 / c
        if axis == "x": y, z = y * k, z * k
        elif axis == "y": x, z = x * k, z * k
        else: x, y = x * k, y * k
    return (x + origin[0], y + origin[1], z + origin[2])


def bs_rotate(p, xr, yr):
    """Blockstate rotation of a point in 0..16 space: x first (north -> down), then y (north -> east)."""
    x, y, z = (c - 8 for c in p)
    for _ in range((xr // 90) % 4): x, y, z = x, z, -y
    for _ in range((yr // 90) % 4): x, y, z = -z, y, x
    return (x + 8, y + 8, z + 8)


def bs_rotate_dir(d, xr, yr):
    n = bs_rotate(tuple(8 + 8 * c for c in NORMAL[d]), xr, yr)
    n = tuple(round((c - 8) / 8) for c in n)
    return next(k for k, v in NORMAL.items() if v == n)


class Jar:
    def __init__(self, path):
        self.z = zipfile.ZipFile(path)
        self.textures = {n[len("assets/minecraft/textures/block/"):-4] for n in self.z.namelist()
                         if n.startswith("assets/minecraft/textures/block/") and n.endswith(".png")}
        self.cache = {}

    def json(self, path):
        return json.loads(self.z.read(f"assets/minecraft/{path}"))

    def model(self, name):
        name = name.replace("minecraft:", "").replace("block/", "")
        if name in self.cache: return self.cache[name]
        try:
            m = self.json(f"models/block/{name}.json")
        except KeyError:
            self.cache[name] = ({}, None); return self.cache[name]
        textures, elements = {}, None
        parent = m.get("parent", "").replace("minecraft:", "").replace("block/", "")
        if parent and parent != "block" and parent != name:
            pt, elements = self.model(parent)
            textures.update(pt)
        textures.update(m.get("textures", {}))
        if "elements" in m: elements = m["elements"]
        self.cache[name] = (textures, elements)
        return self.cache[name]


def resolve(textures, ref):
    for _ in range(10):
        if isinstance(ref, dict): ref = ref.get("sprite")
        if not (isinstance(ref, str) and ref.startswith("#")): break
        ref = textures.get(ref[1:])
    if not isinstance(ref, str): return None
    ref = ref.replace("minecraft:", "")
    return ref[len("block/"):] if ref.startswith("block/") else None


def models_for(blockstate, props):
    """[(model entry, random y-rotation variants?)] that apply to a state."""
    out = []
    if "variants" in blockstate:
        for key, v in blockstate["variants"].items():
            want = dict(kv.split("=") for kv in key.split(",")) if key else {}
            if all(props.get(k) == x for k, x in want.items()):
                entries = v if isinstance(v, list) else [v]
                ys = {e.get("y", 0) for e in entries}
                same = len({(e["model"], e.get("x", 0)) for e in entries}) == 1
                out.append((entries[0], len(entries) > 1 and same and len(ys) > 1))
                break

    def ok(c):
        if "OR" in c: return any(ok(x) for x in c["OR"])
        if "AND" in c: return all(ok(x) for x in c["AND"])
        return all(str(props.get(k)) in str(x).split("|") for k, x in c.items())

    for part in blockstate.get("multipart", []):
        if part.get("when") is None or ok(part["when"]):
            a = part["apply"]
            out.append((a[0] if isinstance(a, list) else a, False))
    return out


def faces_for(jar, base, entry, tinted_suffix):
    """[(corners, uvs, dir, cull, texture, tinted)] for one blockstate model entry, in 0..1 block space."""
    textures, elements = jar.model(entry["model"].split("/")[-1])
    xr, yr, uvlock = entry.get("x", 0), entry.get("y", 0), entry.get("uvlock", False)
    out = []
    for el in elements or []:
        f, t = el["from"], el["to"]
        rot = el.get("rotation")
        for d, face in el.get("faces", {}).items():
            tex = resolve(textures, face.get("texture"))
            if tex is None or tex not in jar.textures: continue  # entity textures etc.
            if "tintindex" in face and tinted_suffix: tex += tinted_suffix
            pts = []
            for c in CORNERS[d]:
                p = tuple(t[i] if c[i] else f[i] for i in range(3))
                if rot and rot.get("angle", 0):
                    p = rotate_axis(p, rot["axis"], rot["angle"], rot["origin"], rot.get("rescale", False))
                pts.append(bs_rotate(p, xr, yr))
            area = math.dist(pts[0], pts[2]) * math.dist(pts[1], pts[3])
            if area < 1e-9: continue
            world_dir = bs_rotate_dir(d, xr, yr)
            if uvlock and (xr or yr):
                # textures stay aligned to the world: default UVs of the rotated box
                lo = [min(p[i] for p in pts) for i in range(3)]
                hi = [max(p[i] for p in pts) for i in range(3)]
                uv = default_uv(world_dir, lo, hi)
                order = [min(range(4), key=lambda j: math.dist(pts[j], tuple(hi[i] if cc[i] else lo[i] for i in range(3))))
                         for cc in CORNERS[world_dir]]
                uvs = [None] * 4
                for k, j in enumerate(order): uvs[j] = corner_uv(uv, 0, k)
            else:
                uv = face.get("uv", default_uv(d, f, t))
                uvs = [corner_uv(uv, face.get("rotation", 0), i) for i in range(4)]
            cull = "cullface" in face and not (rot and rot.get("angle", 0))
            out.append((tuple(tuple(c / 16 for c in p) for p in pts), tuple((u / 16, v / 16) for u, v in uvs),
                        world_dir, cull, tex, "tintindex" in face))
    return out


def fmt(v):
    s = f"{v:.5f}".rstrip("0").rstrip(".")
    return "0" if s in ("-0", "") else s


def load_blocks():
    props = {}
    for m in re.finditer(r'BlockSpec\.Prop (\w+) = new BlockSpec\.Prop\("(\w+)", ([^)]*)\)',
                         open(os.path.join(JAVA, "BlockProperties.java")).read()):
        props[m.group(1)] = (m.group(2), re.findall(r'"([^"]*)"', m.group(3)))
    blocks = []
    for m in re.finditer(r'block\("(\w+)", "(\w+)"((?:, \w+)*)\)([^;]*)',
                         open(os.path.join(JAVA, "BlockDefinitions.java")).read()):
        if "layer(INVISIBLE)" in m.group(4): continue
        plist = [props[p.strip()] for p in m.group(3).split(",") if p.strip()]
        blocks.append((m.group(1), m.group(2), plist))
    return blocks


def main():
    if len(sys.argv) < 2: sys.exit(__doc__)
    jar = Jar(sys.argv[1])

    shapes = {}          # geometry key -> shape name
    shape_lines = []
    def_lines = collections.defaultdict(list)
    imported, skipped = [], []

    for base, family, plist in load_blocks():
        if family in SKIP_FAMILIES: continue
        try:
            blockstate = jar.json(f"blockstates/{base}.json")
        except KeyError:
            skipped.append(base); continue
        names = [p for p, _ in plist]
        results = {}
        for combo in itertools.product(*[v for _, v in plist]):
            state = dict(zip(names, combo))
            faces, random_y = [], False
            for entry, ry in models_for(blockstate, state):
                faces += faces_for(jar, base, entry, TINT_SUFFIX.get(base, ""))
                random_y |= ry
            geo = tuple((c, u, d, cull) for c, u, d, cull, _, _ in faces)
            if geo not in shapes:
                name = "v/empty" if not geo else f"v/{base}_{sum(1 for n in shapes.values() if n.startswith(f'v/{base}_'))}"
                shapes[geo] = name
                verts, vidx, fl = [], {}, []
                for c, u, d, cull, _, _ in faces:
                    ids = []
                    for p in c:
                        key = tuple(round(x, 5) for x in p)
                        if key not in vidx: vidx[key] = len(verts); verts.append(key)
                        ids.append(vidx[key])
                    fl.append(f"{d},{'true' if cull else 'false'}," + ",".join(
                        f"{i},{fmt(uu)},{fmt(vv)}" for i, (uu, vv) in zip(ids, u)))
                shape_lines.append(f"{name}|" + " ".join(",".join(fmt(max(0.0, x)) for x in v) for v in verts)
                                   + "|" + " ".join(fl) + ";")
            tex = [f[4] for f in faces]
            if len(set(tex)) == 1: tex = tex[:1]
            options = " rotate=random" if random_y else ""
            if base in BIOME_TINTED and any(f[5] for f in faces):
                tinted = [i for i, f in enumerate(faces) if f[5]]
                options += " tint=" + ("all" if len(tinted) == len(faces) else ",".join(map(str, tinted)))
            results[combo] = (shapes[geo], tuple(tex), options)
        # states without geometry (e.g. a wall with no post and no sides) get an empty shape; a block
        # where every state is empty is drawn as an entity in vanilla and keeps its existing shape
        filled = [r for r in results.values() if r[1]]
        if filled:
            imported.append(base)
            texture = collections.Counter(t for r in filled for t in r[1]).most_common(1)[0][0]
            results = {c: (r if r[1] else (r[0], (texture,), r[2])) for c, r in results.items()}
            # smallest set of properties that determines shape + textures, then merge values
            for k in range(len(names) + 1):
                for used in itertools.combinations(range(len(names)), k):
                    seen = {}
                    if all(seen.setdefault(tuple(c[i] for i in used), r) == r for c, r in results.items()):
                        break
                else:
                    continue
                break
            if not used:
                shape, tex, options = next(iter(results.values()))
                def_lines[base].append(f"{base} {shape} {' '.join(tex)}{options}")
                continue
            *outer, last = used
            groups = collections.OrderedDict()
            for c, r in results.items():
                groups.setdefault(tuple(c[i] for i in outer), collections.OrderedDict()).setdefault(r, [])
                if c[last] not in groups[tuple(c[i] for i in outer)][r]: groups[tuple(c[i] for i in outer)][r].append(c[last])
            for key, by_result in groups.items():
                for (shape, tex, options), vals in by_result.items():
                    tag = ",".join([f"{names[i]}={v}" for i, v in zip(outer, key)] + [f"{names[last]}={'|'.join(vals)}"])
                    def_lines[base].append(f"{base}[{tag}] {shape} {' '.join(tex)}{options}")
            continue
        skipped.append(base)

    with open(os.path.join(ASSETS, "model", "block", "vanilla.shp"), "w") as fh:
        fh.write("// Nanocraft block shapes imported from the vanilla block models by tools/import_vanilla_models.py;\n")
        fh.write("\n".join(shape_lines) + "\n")
    with open(os.path.join(ASSETS, "def", "block", "vanilla.def"), "w") as fh:
        fh.write("// blocks imported from the vanilla block models by tools/import_vanilla_models.py (don't edit by hand)\n")
        fh.write("// <state pattern> <shape> <texture per face, in the shape's face order>\n")
        for base in imported: fh.write("\n".join(def_lines[base]) + "\n")

    # other .def files lose their lines for the imported blocks
    imported_set = set(imported)
    for f in glob.glob(os.path.join(ASSETS, "def", "block", "*.def")):
        if f.endswith("vanilla.def"): continue
        lines = open(f).read().split("\n")
        keep = [l for l in lines if not l or l.startswith("//") or re.split(r"[\[ ]", l)[0] not in imported_set]
        if len(keep) != len(lines): open(f, "w").write("\n".join(keep))

    idx = os.path.join(ASSETS, "indexes", "blkmdl.idx")
    s = open(idx).read()
    if "model/block/vanilla.shp" not in s: open(idx, "w").write(s.rstrip("\n") + "\nmodel/block/vanilla.shp;\n")
    idx = os.path.join(ASSETS, "indexes", "def.idx")
    s = open(idx).read()
    if "def/block/vanilla.def" not in s: open(idx, "w").write(s.rstrip("\n") + "\ndef/block/vanilla.def\n")

    print(f"imported {len(imported)} blocks into {len(shape_lines)} shapes and "
          f"{sum(map(len, def_lines.values()))} def lines; kept existing shapes for {len(skipped)} blocks:")
    print("  " + " ".join(skipped))


if __name__ == "__main__":
    main()
