package org.sutormin.nanocraft.world;

import org.sutormin.nanocraft.world.chunk.Chunk;

import java.util.List;
import java.util.Map;

/**
 * The dimension the player is in, for its height: the overworld runs from y = -64 to 319, the
 * Nether and the End from 0 to 255.
 *
 * <p>Chunks store block rows from the dimension's lowest y up, so row = world y - {@link #minY()}.
 * That's also the y NanoCraft renders at.
 *
 * <p>Dimension types come from the server's dimension type registry, sent during configuration. The
 * vanilla ones come without data (we tell the server we know the vanilla pack), so their heights are
 * listed here; datapack dimension types carry their own.
 */
public final class Dimension {
    private Dimension() {}

    /** A dimension type's height: blocks from {@code minY} to {@code minY + height - 1}. */
    public record Type(String name, int minY, int height) {}

    // from data/minecraft/dimension_type/*.json in the 26.3 server jar
    private static final Map<String, Type> VANILLA = Map.of(
            "minecraft:overworld", new Type("minecraft:overworld", -64, 384),
            "minecraft:overworld_caves", new Type("minecraft:overworld_caves", -64, 384),
            "minecraft:the_nether", new Type("minecraft:the_nether", 0, 256),
            "minecraft:the_end", new Type("minecraft:the_end", 0, 256));
            //"minecraft:the_sift", new Type("minecraft:the_sift",0,256)); // THE SIFT (UPCOMING);

    private static final Type DEFAULT = VANILLA.get("minecraft:overworld");

    private static volatile List<Type> types = List.of();
    private static volatile Type current = DEFAULT;
    private static volatile String currentName;

    /** A dimension type without data from the server: vanilla's height, or the overworld's if unknown. */
    public static Type vanilla(String name) {
        return VANILLA.getOrDefault(name, new Type(name, DEFAULT.minY(), DEFAULT.height()));
    }

    /** The server's dimension type registry, in id order. */
    public static void setTypes(List<Type> registry) {
        types = List.copyOf(registry);
    }

    /**
     * Called by the join and respawn packets.
     *
     * @return true if this is a different dimension than before, so the old one's chunks must go
     */
    public static boolean enter(int typeId, String dimensionName) {
        List<Type> registry = types;
        Type type = typeId >= 0 && typeId < registry.size() ? registry.get(typeId) : DEFAULT;
        if (type.height() > Chunk.SIZE_Y) {
            System.err.println("[Client] Dimension " + dimensionName + " is " + type.height()
                    + " blocks high; only the lowest " + Chunk.SIZE_Y + " are shown");
        }
        boolean changed = currentName != null && !currentName.equals(dimensionName);
        current = type;
        currentName = dimensionName;
        return changed;
    }

    /** Lowest block y of the current dimension. */
    public static int minY() {
        return current.minY();
    }

    /** Chunk row (and render y) of a world y. */
    public static int toRow(int worldY) {
        return worldY - current.minY();
    }
}
