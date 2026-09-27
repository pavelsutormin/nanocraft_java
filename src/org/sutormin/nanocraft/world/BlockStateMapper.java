package org.sutormin.nanocraft.world;

import org.sutormin.nanocraft.Main;
import org.sutormin.nanocraft.data.Registries;
import org.sutormin.nanocraft.data.quickaccess.QuickAccessBlocks;
import org.sutormin.nanocraft.data.types.Block;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

/**
 * Translates vanilla block state ids into NanoCraft block types.
 *
 * Vanilla ships one id per block state: 35,723 of them in 26.3.
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

    // volatile: load() typically runs on a startup/config thread, map() gets
    // called from packet-handling threads. Without this there's no guarantee
    // another thread ever observes the fully published array.
    private static volatile char[] table;

    private BlockStateMapper() {
    }

    public static void load() {
        try (InputStream in = Main.class.getResourceAsStream("/data/block/blockstates.txt")) {
            load(in);
        } catch (IOException e) {
            System.err.println("ERROR LOADING BLOCKSTATES: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * @param in the generated table, typically
     *           getResourceAsStream("/blockstates.txt")
     */
    public static void load(InputStream in) throws IOException {
        // StringBuilder is backed by a plain char[] internally, so this avoids
        // boxing every entry into a Character the way List<Character> would.
        StringBuilder buffer = new StringBuilder(70_000);

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(in, StandardCharsets.UTF_8)
        )) {
            String line;

            while ((line = reader.readLine()) != null) {
                line = line.trim();

                if (!line.isEmpty()) {
                    buffer.append(fromName(line));
                }
            }
        }

        char[] loaded = new char[buffer.length()];
        buffer.getChars(0, buffer.length(), loaded, 0);
        table = loaded;

        System.out.printf("Loaded %d block states%n", table.length);
    }

    public static boolean isLoaded() {
        return table != null;
    }

    public static char map(int stateId) {
        char[] t = table; // single volatile read, avoids re-reading the field twice below

        if (t == null) {
            // No table yet: show terrain as solid stone, keep air as air.
            return stateId == 0 ? QuickAccessBlocks.AIR : QuickAccessBlocks.STONE;
        }

        if (stateId < 0 || stateId >= t.length) {
            return QuickAccessBlocks.NOT_FOUND;
        }
        return t[stateId];
    }

    /**
     * Everything NanoCraft does not model yet collapses onto the closest
     * block it does have. Extend as BlockTypes grows.
     */
    private static char fromName(String name) {
        int colon = name.indexOf(':');
        if (colon != -1) {
            name = name.substring(colon + 1);
        }

        Block block = Registries.BLOCK.get(name);

        if (block == null) {
            return (char) Registries.BLOCK.get("not_found").getId();
        }

        return (char) block.getId();
    }
}