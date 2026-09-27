#!/usr/bin/env python3
"""
Bakes vanilla's render-time tints into the textures, from a Minecraft client jar.

Vanilla ships some block textures grey and colors them while rendering (by biome, or by redstone power).
NanoCraft doesn't tint, so this writes pre-colored textures instead:

  - ferns, bushes, sugar cane, petal/wildflower stems: forest grass color
  - grass blocks, short grass and tall grass are colored per biome while rendering, so they get
    vanilla's grey textures (and grass_block_side its plain version) instead
  - leaves and vines: the foliage color of the biome each tree grows in most (oak: forest,
    jungle and vines: jungle, acacia: savanna, dark oak: dark forest, mangrove: mangrove swamp);
    birch and spruce leaves and lily pads have vanilla's fixed colors
  - leaf litter: plains dry foliage (brown)
  - water (still, flowing and in cauldrons): plains water color
  - melon/pumpkin stems: one copy per growth age, <texture>_0 .. <texture>_7 (vanilla.def uses
    <texture>_{age}); attached stems (next to a grown fruit) get vanilla's fixed color
  - redstone dust: one copy per power level, <texture>_0 .. <texture>_15 (vanilla.def uses <texture>_{power})

Only faces vanilla actually tints (tintindex in the block models) are colored, and the colored textures
replace the grey ones under the same name. Colors come from the jar's colormaps, like vanilla.

Usage:  python3 tools/tinted_textures.py path/to/client.jar [output dir]
Output defaults to src/resources/assets/texture/block. Needs Pillow (pip install pillow).
"""
import io
import os
import sys
import zipfile

from PIL import Image

sys.path.insert(0, os.path.dirname(os.path.abspath(__file__)))
from import_vanilla_models import Jar, models_for, resolve  # noqa: E402

# biome climate (temperature, downfall) for colormap lookups
PLAINS, FOREST, JUNGLE, SAVANNA, DARK_FOREST = (0.8, 0.4), (0.7, 0.8), (0.95, 0.9), (2.0, 0.0), (0.7, 0.8)

# block -> (colormap or None, climate or fixed RGB): vanilla's BlockColors, with the chosen biome
TINTS = {
    **{b: ("grass", FOREST) for b in ("fern", "large_fern", "potted_fern",
                                       "bush", "pink_petals", "wildflowers", "sugar_cane")},  # darker than plains
    "oak_leaves": ("foliage", FOREST),
    "jungle_leaves": ("foliage", JUNGLE),
    "vine": ("foliage", JUNGLE),
    "acacia_leaves": ("foliage", SAVANNA),
    "dark_oak_leaves": ("foliage", DARK_FOREST),
    "mangrove_leaves": (None, (0x8D, 0xB1, 0x27)),  # mangrove swamp's foliage color override
    "birch_leaves": (None, (0x80, 0xA7, 0x55)),
    "spruce_leaves": (None, (0x61, 0x99, 0x61)),
    "lily_pad": (None, (0x20, 0x80, 0x30)),
    "leaf_litter": ("dry_foliage", PLAINS),
    "water": (None, (0x3F, 0x76, 0xE4)),  # plains water color
    "water_cauldron": (None, (0x3F, 0x76, 0xE4)),
    "attached_melon_stem": (None, (0xE0, 0xC7, 0x1C)),
    "attached_pumpkin_stem": (None, (0xE0, 0xC7, 0x1C)),
}
# tinted per biome at runtime (def option tint=): these keep vanilla's grey textures
BIOME_TINTED = ["grass_block", "short_grass", "tall_grass"]
# growing stems are colored by age instead, one texture per age
STEMS = ["melon_stem", "pumpkin_stem"]
# fluids are drawn without a block model, so their tinted textures are listed here
FLUID_TEXTURES = {"water": {"water_still", "water_flow"}}
REDSTONE = ["redstone_dust_dot", "redstone_dust_line0", "redstone_dust_line1"]


def colormap_color(z, name, climate):
    """Vanilla's GrassColor/FoliageColor lookup: downfall is scaled by temperature, both clamped to 0..1."""
    cmap = Image.open(io.BytesIO(z.read(f"assets/minecraft/textures/colormap/{name}.png"))).convert("RGB")
    t = min(max(climate[0], 0.0), 1.0)
    d = min(max(climate[1], 0.0), 1.0) * t
    return cmap.getpixel((int((1 - t) * 255), int((1 - d) * 255)))


def redstone_color(power):
    """Vanilla's redstone wire color for a power level (RedStoneWireBlock)."""
    f = power / 15.0
    r = f * 0.6 + (0.4 if f > 0 else 0.3)
    g = min(max(f * f * 0.7 - 0.5, 0.0), 1.0)
    b = min(max(f * f * 0.6 - 0.7, 0.0), 1.0)
    return r * 255, g * 255, b * 255


def stem_color(age):
    """Vanilla's melon/pumpkin stem color for a growth age (0-7)."""
    return age * 32, 255 - age * 8, age * 4


def tint(img, rgb):
    """Multiplies a texture by a color, like vanilla's vertex tint."""
    out = Image.new("RGBA", img.size)
    out.putdata([(round(p[0] * rgb[0] / 255), round(p[1] * rgb[1] / 255), round(p[2] * rgb[2] / 255), p[3])
                 for p in img.getdata()])
    return out


def tinted_textures(jar, block):
    """Textures on the faces vanilla tints, across all of the block's models."""
    blockstate = jar.json(f"blockstates/{block}.json")
    entries = []
    for v in blockstate.get("variants", {}).values():
        entries += v if isinstance(v, list) else [v]
    for part in blockstate.get("multipart", []):
        a = part["apply"]
        entries += a if isinstance(a, list) else [a]
    out = set()
    for e in entries:
        textures, elements = jar.model(e["model"].split("/")[-1])
        for el in elements or []:
            for face in el.get("faces", {}).values():
                if "tintindex" in face:
                    t = resolve(textures, face.get("texture"))
                    if t in jar.textures: out.add(t)
    return out


def main():
    if len(sys.argv) < 2:
        sys.exit(__doc__)
    out_dir = sys.argv[2] if len(sys.argv) > 2 else os.path.join(
        os.path.dirname(os.path.abspath(__file__)), "..", "src", "resources", "assets", "texture", "block")
    os.makedirs(out_dir, exist_ok=True)
    jar = Jar(sys.argv[1])
    z = jar.z

    def texture(name):
        return Image.open(io.BytesIO(z.read(f"assets/minecraft/textures/block/{name}.png"))).convert("RGBA")

    for block, (cmap, value) in TINTS.items():
        rgb = colormap_color(z, cmap, value) if cmap else value
        names = tinted_textures(jar, block) | FLUID_TEXTURES.get(block, set())
        for name in sorted(names):
            if name == "grass_block_side_overlay":
                continue  # painted onto grass_block_side below
            tint(texture(name), rgb).save(os.path.join(out_dir, f"{name}.png"))
        print(f"{block:16} #{int(rgb[0]):02X}{int(rgb[1]):02X}{int(rgb[2]):02X}  {' '.join(sorted(names - {'grass_block_side_overlay'}))}")

    for block in BIOME_TINTED:
        names = tinted_textures(jar, block) | ({"grass_block_side"} if block == "grass_block" else set())
        for name in sorted(names):
            texture(name).save(os.path.join(out_dir, f"{name}.png"))
        print(f"{block:16} biome tint   {' '.join(sorted(names))} (grey, as in vanilla)")

    for block in STEMS:
        for name in sorted(tinted_textures(jar, block)):
            grey = texture(name)
            for age in range(8):
                tint(grey, stem_color(age)).save(os.path.join(out_dir, f"{name}_{age}.png"))
            print(f"{block:16} age 0..7     {name}_{{age}}")

    for name in REDSTONE:
        grey = texture(name)
        for power in range(16):
            tint(grey, redstone_color(power)).save(os.path.join(out_dir, f"{name}_{power}.png"))
    print(f"redstone dust    power 0..15  {' '.join(n + '_{power}' for n in REDSTONE)}")


if __name__ == "__main__":
    main()
