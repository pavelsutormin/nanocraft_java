# Getting started

How to set up NanoCraft from a fresh clone: generate the textures, run a local Minecraft server and
connect to it with NanoCraft.

NanoCraft talks to **Minecraft 26.3** servers (protocol 777) in **offline mode**; it doesn't log in to
Microsoft accounts.

## Requirements

- **Java 25** (the Gradle wrapper runs on it too).
- **Linux**: the build pulls LWJGL's `natives-linux`. For another OS, change `lwjglNatives` in
  `build.gradle.kts` (e.g. `natives-windows`, `natives-macos-arm64`).
- **Python 3 with Pillow** (`pip install pillow`), for the texture and data tools.
- An internet connection the first time, to download the Minecraft client/server jars from Mojang.

Everything downloaded from Mojang goes into `mojang/`, and all generated textures into
`src/resources/assets/texture/`. Both are gitignored: Mojang's assets never end up in the repository.

## 1. Generate the textures

A fresh clone has no block textures (only `null.png`, the purple-black "missing texture"). Make them
from the official client jar:

```sh
./gradlew copyBlockTextures generateTextures
```

This:

1. downloads the Minecraft 26.3 client jar to `mojang/client/` (checked against Mojang's SHA-1) and
   extracts its assets there (`downloadClient`),
2. copies every vanilla block texture into `src/resources/assets/texture/block/`
   (`copyBlockTextures`),
3. writes the textures NanoCraft colors ahead of time (`generateTextures`): leaves, vines, water,
   ferns, redstone dust per power level, melon/pumpkin stems per age, and block-style chest textures.

Run it again after changing Minecraft version, or whenever textures look wrong or missing. It
overwrites same-named files in the texture folder.

Blocks whose texture is missing render with `null.png`. At startup the game prints how many textures
were missing: a big number means step 1 hasn't been run.

## 2. Run a local server

```sh
./gradlew runServer
```

The first run downloads the 26.3 server jar to `mojang/server/` and then stops, asking you to accept
the Minecraft EULA: read <https://aka.ms/MinecraftEULA> and, if you agree, set `eula=true` in
`mojang/server/eula.txt`, then run `./gradlew runServer` again.

Every start, the task sets these in `mojang/server/server.properties` (other settings are yours to
change):

| setting          | value       | why                                                                       |
|------------------|-------------|---------------------------------------------------------------------------|
| `online-mode`    | `false`     | NanoCraft doesn't log in to Microsoft accounts                            |
| `server-port`    | `25565`     | the port NanoCraft connects to by default                                 |
| `white-list`     | `false`     | any username can join                                                     |
| `gamemode`       | `spectator` | NanoCraft's camera flies through everything, like spectator mode          |
| `force-gamemode` | `true`      | everyone joins in spectator, even players who were in another mode before |
| `allow-flight`   | `true`      | otherwise the server kicks players for floating                           |

Type server commands (`stop`, `time set day`, `gamemode ...`) into the Gradle console. The world is
saved in `mojang/server/world/`; delete that folder for a new world.

## 3. Run NanoCraft

With the server running, in a second terminal:

```sh
./gradlew runJar
```

This builds a jar with all dependencies (`build/libs/`) and starts it. You can also run
`org.sutormin.nanocraft.Main` straight from your IDE.

The first start writes `options.yaml` (see [Settings](#settings)): set your `username` there.

### Controls

| key          | action               |
|--------------|----------------------|
| mouse        | look around          |
| W A S D      | move                 |
| Space        | up                   |
| Left Shift   | down                 |
| Left Control | faster (hold)        |
| Escape       | quit                 |

### Settings

Settings are read from `options.yaml` in the working directory (the project folder with `runJar`).
NanoCraft writes it with the defaults and a comment for every option on the first start; it's
gitignored, so everyone keeps their own. Restart NanoCraft after changing it.

```yaml
CONFIG_VERSION: 2

SERVER:
  ADDRESS: "127.0.0.1"
  PORT: 25565

PLAYER:
  USERNAME: "Player"
...
```

| option                           | default              | meaning                                           |
|----------------------------------|----------------------|---------------------------------------------------|
| `SERVER.ADDRESS`, `SERVER.PORT`  | `127.0.0.1`, `25565` | the server to connect to                          |
| `PLAYER.USERNAME`                | `Player`             | your name on the server (3-16 letters, digits or `_`) |
| `GRAPHICS.VIEW_DISTANCE`         | `10`                 | chunks the server sends around you (2-32)         |
| `GRAPHICS.TRANSPARENT_LEAVES`    | `false`              | see-through leaves like vanilla's "fancy" leaves (slower); off draws them opaque |
| `PERFORMANCE.CHUNKS_PER_TICK`    | `5.0`                | chunks per tick the server should send you (0.01-64; the server caps it at 64) |
| `PERFORMANCE.MESH_THREADS`       | cores - 1 (1-8)      | background threads building chunk meshes          |
| `PERFORMANCE.MESH_UPLOAD_BUDGET_MS` | `4.0`                | time per frame spent uploading chunks to the GPU  |

To join a server on another machine, set `SERVER.ADDRESS` to its address; it has to be a 26.3
server in offline mode.

Options that are missing from the file (e.g. ones added in a newer NanoCraft) use their defaults and
are added to the file, so every option shows up there; the file before is kept as
`options.yaml.bak`. Unknown options and values that don't fit (a port of 99999, `maybe` for a
true/false option) print a warning and are ignored; while there are any, the file isn't rewritten. True/false options take only
`true` or `false` (not `yes`/`no`), so a username like `yes` stays a name.

#### CONFIG_VERSION and migration

`CONFIG_VERSION` is the layout of the file. When NanoCraft finds a file from an older layout, it
carries your values over to the new one, keeps the old file as a backup (e.g.
`options.yaml.v2.bak`) and writes a fresh `options.yaml`. That includes `options.txt`, the
`key=value` file from before `options.yaml` (`CONFIG_VERSION` 1): it's migrated on the first start and
kept as `options.txt.bak`. A file from a newer NanoCraft is read as far as possible and left alone.

For developers: adding an option only needs a new entry in `SECTIONS` in `Options.java`; files
without it get it filled in. To rename, move or remove options, bump `CONFIG_VERSION`, change
`SECTIONS`, and add a step to `MIGRATIONS` that turns the previous version's values into the new ones.

#### Debug options

In the `DEBUG` section; all `true`/`false`, default `false`:

| option                    | does                                                                                |
|---------------------------|-------------------------------------------------------------------------------------|
| `LOG_UNKNOWN_S2C_PACKETS` | prints each server packet NanoCraft doesn't handle (it's skipped): phase, id, size |
| `LOG_KNOWN_S2C_PACKETS`   | prints each server packet NanoCraft handles, with its class (chunks make this busy) |
| `LOG_C2S_PACKETS`         | prints each packet sent to the server, with its class (20 a second while playing)  |
| `LOG_MISSING_TEXTURES`    | prints each texture that isn't found, instead of just how many                     |
| `SHOW_FPS`                | shows FPS, loaded chunks and your position in the window title                     |
| `WIREFRAME`               | draws the world as wireframe triangles                                              |

Packet logs look like `[S2C] PLAY 0x26 S2CUnloadChunk (8 bytes)`: ids are in hex, as on
<https://minecraft.wiki/w/Java_Edition_protocol>.

## Gradle tasks

`./gradlew tasks` lists them all. The NanoCraft-specific ones:

**mojang**

| task               | does                                                                                  |
|--------------------|---------------------------------------------------------------------------------------|
| `downloadClient`   | downloads the client jar and extracts its assets to `mojang/client/`                  |
| `downloadServer`   | downloads the server jar to `mojang/server/`                                          |
| `runServer`        | runs the server (see above)                                                           |
| `generateReports`  | runs vanilla's data generator; reports go to `build/mojang/datagen/reports/`          |

**tools** (Python scripts in `tools/`, run on the client jar)

| task                  | does                                                                                        |
|-----------------------|---------------------------------------------------------------------------------------------|
| `copyBlockTextures`   | copies vanilla's block textures into `src/resources/assets/texture/block/`                  |
| `generateTextures`    | runs `chestTextures` and `tintedTextures`                                                   |
| `chestTextures`       | block-style chest textures from vanilla's chest entity textures                             |
| `tintedTextures`      | pre-colored foliage, water, stems and redstone textures                                     |
| `importVanillaModels` | regenerates `model/block/vanilla.shp` and `def/block/vanilla.def` from vanilla block models |
| `biomeColors`         | regenerates `data/biome/grass_colors.txt` from vanilla's biomes                             |
| `generateBlockstates` | regenerates `data/block/blockstates.txt` (block state ids) from the data generator          |

**application**

| task     | does                               |
|----------|------------------------------------|
| `runJar` | builds and runs the NanoCraft jar  |

## Updating to a new Minecraft version

1. Set `minecraftVersion` in `build.gradle.kts`.
2. Update the protocol version and any changed packet ids/layouts in
   `src/org/sutormin/nanocraft/networking/` (`PacketList.java` maps packet ids to classes).
3. Regenerate the data: `./gradlew generateBlockstates biomeColors importVanillaModels`.
4. Add definitions for new blocks in `data/definitions/block/BlockDefinitions.java`.
5. Regenerate the textures: `./gradlew copyBlockTextures generateTextures`.
6. Delete `mojang/server/world/` if the old world doesn't load, and run the server and game again.

## How blocks are drawn

All under `src/resources/assets/`:

- **`def/block/*.def`**: which shape and textures each block state uses, one per line:

  ```
  <name> <shape> <textures...> [options]
  grass_block[snowy=true] full grass_block_top dirt grass_block_snow grass_block_snow grass_block_snow grass_block_snow
  ```

  - Full blocks list textures as up, down, north, south, east, west; one texture covers every face.
  - `name[prop=a|b]` matches every state with those property values, and the most specific match
    wins. `@default` (in `default.def`) catches blocks nothing else matches.
  - In texture names, `*` is the block's name, `^` its state tag, and `{prop}` a property's value.
  - Options: `rotate=random|random_all|mirror` (random texture turns per block), `uv=` (per-face
    texture transforms), `tint=<faces>|all` (colored by the biome's grass color).
  - The comments at the top of each file explain what's in it.
- **`model/block/*.shp`**: block shapes (faces and their vertices), matched by the block's state.
  `vanilla.shp` is generated from vanilla's models by `importVanillaModels`.
- **`indexes/def.idx`** and **`indexes/blkmdl.idx`**: which `.def` and `.shp` files get loaded, one
  path per line (`blkmdl.idx` lines end with `;`). List new files there.
- **`texture/block/*.png`**: the textures (generated, see step 1). Only the top-left 16x16 pixels of
  each image are used.
