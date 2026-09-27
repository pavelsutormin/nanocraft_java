#!/usr/bin/env python3
"""
Writes src/resources/data/block/blockstates.txt from the vanilla data generator's blocks report:
one line per block state id, in id order, e.g. "minecraft:oak_stairs[facing=north,half=top,...]".
BlockStateMapper uses it to turn the state ids in chunk data into NanoCraft blocks.

Usage:  python3 tools/blockstates.py path/to/reports/blocks.json
(the report comes from the server jar: ./gradlew generateBlockstates does both steps)
"""
import json
import os
import sys

OUT = os.path.join(os.path.dirname(os.path.abspath(__file__)), "..", "src", "resources", "data", "block", "blockstates.txt")


def main():
    if len(sys.argv) < 2:
        sys.exit(__doc__)
    blocks = json.load(open(sys.argv[1]))
    states = []
    for name, block in blocks.items():
        for state in block["states"]:
            props = state.get("properties", {})
            tag = "[" + ",".join(f"{k}={v}" for k, v in props.items()) + "]" if props else ""
            states.append((state["id"], name + tag))
    states.sort()
    if [i for i, _ in states] != list(range(len(states))):
        sys.exit("block state ids in the report aren't contiguous")
    with open(OUT, "w") as fh:
        fh.write("\n".join(name for _, name in states) + "\n")
    print(f"wrote {len(states)} block states to {os.path.relpath(OUT)}")


if __name__ == "__main__":
    main()
