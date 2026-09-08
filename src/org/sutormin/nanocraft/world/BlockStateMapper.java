package org.sutormin.nanocraft.world;

import org.sutormin.nanocraft.block.BlockRegistry;
import org.sutormin.nanocraft.block.BlockType;
import org.sutormin.nanocraft.block.BlockTypes;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * Translates vanilla block state ids into NanoCraft block types.
 *
 * Vanilla ships one id per block state, so 26.2 has more than 65536 of them.
 * The mapping is version specific and cannot be hardcoded; it is loaded from a
 * generated table of state names, one line per state id in ascending order:
 *
 *   minecraft:air
 *   minecraft:stone
 *   minecraft:granite
 *   ...
 *
 * Generate it from the server jar with the vanilla data generator:
 *
 *   java -DbundlerMainClass=net.minecraft.data.Main -jar server.jar --reports
 *   jq -r '[to_entries[] | .key as $n | .value.states[] | {id, name: $n}]
 *          | sort_by(.id) | .[].name' \
 *      generated/reports/blocks.json > blockstates.txt
 *
 * Until the table is loaded, every non-air state falls back to STONE, which is
 * enough to confirm the packet parser works before touching registry data.
 */
public final class BlockStateMapper {

    private static char[] table;

    private BlockStateMapper() {
    }

    /**
     * @param in the generated table, typically
     *           getResourceAsStream("/blockstates.txt")
     */
    public static void load(InputStream in) throws IOException {
        List<Character> types = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(in, StandardCharsets.UTF_8)
        )) {
            String line;

            while ((line = reader.readLine()) != null) {
                line = line.trim();

                if (!line.isEmpty()) {
                    types.add(fromName(line));
                }
            }
        }

        char[] loaded = new char[types.size()];

        for (int i = 0; i < loaded.length; i++) {
            loaded[i] = types.get(i);
        }

        table = loaded;

        System.out.printf("Loaded %d block states%n", table.length);
    }

    public static boolean isLoaded() {
        return table != null;
    }

    public static char map(int stateId) {
        if (table == null) {
            // No table yet: show terrain as solid stone, keep air as air.
            return stateId == 0 ? BlockTypes.AIR : BlockTypes.STONE;
        }

        if (stateId < 0 || stateId >= table.length) {
            return BlockTypes.STONE;
        }

        return table[stateId];
    }

    /**
     * Everything NanoCraft does not model yet collapses onto the closest
     * block it does have. Extend as BlockTypes grows.
     */
    private static char fromName(String name) {
      BlockType block = BlockRegistry.getBlockByName(name);

      if (block == null) {
        //System.err.println("Unknown block: " + name);
        return BlockTypes.NULL;
      }

      return block.getId();
    }

    /**
     * Crude filter so plants, torches and similar do not become stone cubes.
     * A real implementation would read the collision shapes from the reports.
     */
    private static boolean isNonSolid(String name) {
        return name.endsWith("_sapling")
                || name.endsWith("_flower")
                || name.endsWith("_fern")
                || name.endsWith("_grass")
                || name.endsWith("_bush")
                || name.endsWith("_torch")
                || name.endsWith("_sign")
                || name.endsWith("_banner")
                || name.endsWith("_carpet")
                || name.endsWith("_pressure_plate")
                || name.endsWith("_button")
                || name.contains("lava")
                || name.contains("fire")
                || name.contains("rail")
                || name.contains("vine")
                || name.contains("mushroom")
                || name.contains("coral_fan")
                || name.contains("seagrass")
                || name.contains("kelp");
    }
}