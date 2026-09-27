package org.sutormin.nanocraft.world.biome;

import org.sutormin.nanocraft.Main;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Biome grass colors, picked per block like vanilla with biome blend 0.
 *
 * <p>Colors come from data/biome/grass_colors.txt (made by tools/biome_colors.py from the vanilla
 * biome definitions): a fixed color per biome, with the dark forest modifier already applied, or
 * "swamp" for biomes whose color vanilla picks from a noise at each block. Biome ids are the order of
 * the server's biome registry, sent during configuration.
 *
 * <p>Which biome a block uses isn't simply its 4x4x4 cell: vanilla's BiomeManager blurs cell borders
 * with a hash of the world seed (sent as the "hashed seed" when joining), so this does the same.
 */
public final class BiomeTint {
    private BiomeTint() {}

    private static final int PLAINS = 0x91BD59;
    private static final int SWAMP_COLD = 0x4C763C;
    private static final int SWAMP_WARM = 0x6A7039;

    private static final Map<String, Integer> COLORS_BY_NAME = new HashMap<>(); // -1 = swamp
    private static volatile int[] colorById = new int[0];
    private static volatile long zoomSeed;

    public static void load() {
        try (InputStream in = Main.class.getResourceAsStream("/data/biome/grass_colors.txt")) {
            if (in == null) throw new RuntimeException("Resource /data/biome/grass_colors.txt not found");
            BufferedReader reader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8));
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("//")) continue;
                String[] parts = line.split("\\s+");
                COLORS_BY_NAME.put(parts[0], parts[1].equals("swamp") ? -1 : Integer.parseInt(parts[1], 16));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /** The server's biome registry, in id order (e.g. "minecraft:plains"). Unknown biomes get plains. */
    public static void setRegistry(List<String> names) {
        int[] colors = new int[names.size()];
        for (int i = 0; i < colors.length; i++) {
            colors[i] = COLORS_BY_NAME.getOrDefault(names.get(i), PLAINS);
        }
        colorById = colors;
    }

    /** The "hashed seed" from the join/respawn packet. */
    public static void setZoomSeed(long seed) {
        zoomSeed = seed;
    }

    /** Grass color (0xRRGGBB) of a biome at a block's world x/z. */
    public static int grassColor(int biomeId, int worldX, int worldZ) {
        int[] colors = colorById;
        int color = biomeId >= 0 && biomeId < colors.length ? colors[biomeId] : PLAINS;
        if (color != -1) return color;
        // vanilla (26.3) gets the noise as a float before comparing, so round the same way
        float noise = (float) SWAMP_NOISE.getValue(worldX * 0.0225, worldZ * 0.0225);
        return noise < -0.1 ? SWAMP_COLD : SWAMP_WARM;
    }

    // ------------------------------------------------------------------
    // Biome zoom (vanilla BiomeManager.getBiome)
    // ------------------------------------------------------------------

    /**
     * The 4x4x4 biome cell a block takes its biome from, as world cell coordinates packed into
     * {x, y, z}. Vanilla picks the nearest of the 8 surrounding cell corners after jittering each
     * corner with a hash of the seed.
     */
    public static void zoom(int worldX, int worldY, int worldZ, int[] out) {
        int i = worldX - 2, j = worldY - 2, k = worldZ - 2;
        int l = i >> 2, m = j >> 2, n = k >> 2;
        double d = (i & 3) / 4.0, e = (j & 3) / 4.0, f = (k & 3) / 4.0;
        long seed = zoomSeed;
        int best = 0;
        double bestDist = Double.POSITIVE_INFINITY;
        for (int p = 0; p < 8; p++) {
            boolean bx = (p & 4) == 0, by = (p & 2) == 0, bz = (p & 1) == 0;
            double dist = fiddledDistance(seed, bx ? l : l + 1, by ? m : m + 1, bz ? n : n + 1,
                    bx ? d : d - 1.0, by ? e : e - 1.0, bz ? f : f - 1.0);
            if (bestDist > dist) {
                best = p;
                bestDist = dist;
            }
        }
        out[0] = (best & 4) == 0 ? l : l + 1;
        out[1] = (best & 2) == 0 ? m : m + 1;
        out[2] = (best & 1) == 0 ? n : n + 1;
    }

    private static double fiddledDistance(long seed, int x, int y, int z, double xFrac, double yFrac, double zFrac) {
        long l = lcg(seed, x);
        l = lcg(l, y);
        l = lcg(l, z);
        l = lcg(l, x);
        l = lcg(l, y);
        l = lcg(l, z);
        double dx = fiddle(l);
        l = lcg(l, seed);
        double dy = fiddle(l);
        l = lcg(l, seed);
        double dz = fiddle(l);
        return square(zFrac + dz) + square(yFrac + dy) + square(xFrac + dx);
    }

    private static long lcg(long seed, long salt) {
        seed *= seed * 6364136223846793005L + 1442695040888963407L;
        return seed + salt;
    }

    private static double fiddle(long seed) {
        double d = Math.floorMod(seed >> 24, 1024) / 1024.0;
        return (d - 0.5) * 0.9;
    }

    private static double square(double v) {
        return v * v;
    }

    // ------------------------------------------------------------------
    // Swamp grass noise (vanilla Biome.BIOME_INFO_NOISE: 2D simplex noise, seed 2345)
    // ------------------------------------------------------------------

    private static final Simplex SWAMP_NOISE = new Simplex(new Random(2345L));

    private static final class Simplex {
        private static final int[][] GRADIENT = {
                {1, 1, 0}, {-1, 1, 0}, {1, -1, 0}, {-1, -1, 0}, {1, 0, 1}, {-1, 0, 1}, {1, 0, -1}, {-1, 0, -1},
                {0, 1, 1}, {0, -1, 1}, {0, 1, -1}, {0, -1, -1}, {1, 1, 0}, {0, -1, 1}, {-1, 1, 0}, {0, -1, -1}};
        private static final double F2 = 0.5 * (Math.sqrt(3.0) - 1.0);
        private static final double G2 = (3.0 - Math.sqrt(3.0)) / 6.0;
        private final int[] p = new int[512];

        /** java.util.Random draws the same numbers as vanilla's LegacyRandomSource. */
        Simplex(Random random) {
            random.nextDouble(); // x/y/z offsets, unused in 2D but they advance the random
            random.nextDouble();
            random.nextDouble();
            for (int i = 0; i < 256; i++) p[i] = i;
            for (int i = 0; i < 256; i++) {
                int j = random.nextInt(256 - i);
                int t = p[i];
                p[i] = p[j + i];
                p[j + i] = t;
            }
        }

        private int p(int i) {
            return p[i & 255];
        }

        private static double corner(int g, double x, double y, double max) {
            double d = max - x * x - y * y;
            if (d < 0.0) return 0.0;
            d *= d;
            return d * d * (GRADIENT[g][0] * x + GRADIENT[g][1] * y);
        }

        double getValue(double x, double y) {
            double f = (x + y) * F2;
            int i = (int) Math.floor(x + f);
            int j = (int) Math.floor(y + f);
            double g = (i + j) * G2;
            double l = x - (i - g);
            double m = y - (j - g);
            int n, o;
            if (l > m) { n = 1; o = 0; } else { n = 0; o = 1; }
            double q1 = l - n + G2, q2 = m - o + G2;
            double r1 = l - 1.0 + 2.0 * G2, r2 = m - 1.0 + 2.0 * G2;
            int t = i & 255, u = j & 255;
            int a = p(t + p(u)) % 12;
            int b = p(t + n + p(u + o)) % 12;
            int c = p(t + 1 + p(u + 1)) % 12;
            return 70.0 * (corner(a, l, m, 0.5) + corner(b, q1, q2, 0.5) + corner(c, r1, r2, 0.5));
        }
    }
}
