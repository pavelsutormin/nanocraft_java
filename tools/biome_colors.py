#!/usr/bin/env python3
"""
Writes the grass color of every vanilla biome, from a Minecraft client jar, to
src/resources/data/biome/grass_colors.txt, which BiomeTint loads at startup.

A biome's color is its grass_color override, or the grass colormap at its temperature and downfall
(like vanilla's Biome.getGrassColor), with the dark forest modifier applied. Biomes with the swamp
modifier are written as "swamp": vanilla picks their color from a noise per block, done in BiomeTint.

Usage:  python3 tools/biome_colors.py path/to/client.jar
Needs Pillow (pip install pillow).
"""
import io
import json
import os
import sys
import zipfile

from PIL import Image

OUT = os.path.join(os.path.dirname(os.path.abspath(__file__)), "..", "src", "resources", "data", "biome", "grass_colors.txt")
PREFIX = "data/minecraft/worldgen/biome/"


def colormap_color(cmap, temperature, downfall):
    """Vanilla's GrassColor.get: both clamped to 0..1, downfall scaled by temperature."""
    t = min(max(temperature, 0.0), 1.0)
    d = min(max(downfall, 0.0), 1.0) * t
    r, g, b = cmap.getpixel((int((1 - t) * 255), int((1 - d) * 255)))
    return (r << 16) | (g << 8) | b


def main():
    if len(sys.argv) < 2:
        sys.exit(__doc__)
    with zipfile.ZipFile(sys.argv[1]) as z:
        cmap = Image.open(io.BytesIO(z.read("assets/minecraft/textures/colormap/grass.png"))).convert("RGB")
        lines = []
        for name in sorted(n for n in z.namelist() if n.startswith(PREFIX) and n.endswith(".json")):
            biome = json.loads(z.read(name))
            effects = biome.get("effects", {})
            modifier = effects.get("grass_color_modifier", "none")
            if modifier == "swamp":
                color = "swamp"
            else:
                c = effects.get("grass_color")
                c = int(c.lstrip("#"), 16) if isinstance(c, str) else c
                if c is None:
                    c = colormap_color(cmap, biome["temperature"], biome["downfall"])
                if modifier == "dark_forest":
                    c = ((c & 0xFEFEFE) + 0x28340A) >> 1
                color = f"{c:06x}"
            lines.append(f"minecraft:{name[len(PREFIX):-len('.json')]} {color}")

    os.makedirs(os.path.dirname(OUT), exist_ok=True)
    with open(OUT, "w") as fh:
        fh.write("// grass color per biome (hex RGB, or swamp = picked per block from a noise); "
                 "made by tools/biome_colors.py\n")
        fh.write("\n".join(lines) + "\n")
    print(f"wrote {len(lines)} biomes to {os.path.relpath(OUT)}")


if __name__ == "__main__":
    main()
