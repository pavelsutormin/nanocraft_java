package org.sutormin.nanocraft.data.definitions.block;

import org.sutormin.nanocraft.Options;
import org.sutormin.nanocraft.data.registry.Registry;
import org.sutormin.nanocraft.data.types.Block;
import org.sutormin.nanocraft.data.types.BlockShape;
import org.sutormin.nanocraft.data.types.Block.RenderLayer;
import org.sutormin.nanocraft.resources.block.BlockDefinitionParser;
import org.sutormin.nanocraft.resources.block.BlockShapeParser;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Describes one vanilla block (e.g. "oak_stairs") and registers one {@link Block} per state
 * (e.g. "oak_stairs[facing=north,half=top,shape=straight,waterlogged=false]").
 */
public final class BlockSpec {

    /** A block-state property and its possible values, in vanilla order. */
    public static final class Prop {
        final String name;
        final String[] values;
        public Prop(String name, String... values) { this.name = name; this.values = values; }
    }

    private final String name;
    private final String family;
    private final Prop[] props;

    private float hardness = 0f;
    private String tool = null;
    private String sound = "stone";
    private RenderLayer layer = RenderLayer.OPAQUE;
    private int opacity = 15;
    private int light = 0;
    private boolean solid = true;
    private boolean liquid = false;
    private boolean climbable = false;
    private float friction = 0.6f;
    private float speed = 1.0f;

    private BlockSpec(String name, String family, Prop[] props) {
        this.name = name;
        this.family = family;
        this.props = props;
    }

    /**
     * @param name   vanilla block id without namespace
     * @param family shape family, used as the BLOCK_SHAPE key (slabs pick slab_top / slab_bottom / full per state)
     */
    public static BlockSpec block(String name, String family, Prop... props) {
        return new BlockSpec(name, family, props);
    }

    // ---- builder ----
    public BlockSpec hardness(float h) { this.hardness = h; return this; }
    public BlockSpec unbreakable() { this.hardness = -1f; return this; }
    public BlockSpec tool(String tool) { this.tool = tool; return this; }
    public BlockSpec sound(String sound) { this.sound = sound; return this; }
    public BlockSpec layer(RenderLayer layer) { this.layer = layer; return this; }
    public BlockSpec opacity(int opacity) { this.opacity = opacity; return this; }
    /** Light when "on". State-dependent blocks (lit, candles, pickles, ...) are handled in lightFor. */
    public BlockSpec light(int light) { this.light = light; return this; }
    public BlockSpec noCollision() { this.solid = false; return this; }
    public BlockSpec liquid() { this.liquid = true; return this; }
    public BlockSpec climbable() { this.climbable = true; return this; }
    public BlockSpec friction(float f) { this.friction = f; return this; }
    public BlockSpec speed(float s) { this.speed = s; return this; }

    // ---- registration ----
    public void register(Registry<Block> reg) {
        if (props.length == 0) {
            configure(reg.addNew(name), name, Map.of());
            return;
        }
        int[] idx = new int[props.length];
        while (true) {
            Map<String, String> state = new LinkedHashMap<>();
            StringBuilder sb = new StringBuilder(name).append('[');
            for (int i = 0; i < props.length; i++) {
                String value = props[i].values[idx[i]];
                state.put(props[i].name, value);
                if (i > 0) sb.append(',');
                sb.append(props[i].name).append('=').append(value);
            }
            String stateName = sb.append(']').toString();
            configure(reg.addNew(stateName), stateName, state);

            int p = props.length - 1;           // advance like an odometer
            while (p >= 0 && ++idx[p] == props[p].values.length) {
                idx[p] = 0;
                p--;
            }
            if (p < 0) return;
        }
    }

    private void configure(Block b, String stateName, Map<String, String> s) {
        BlockDefinitionParser.BlockDefinition def = layer != RenderLayer.INVISIBLE ? BlockDefinitionParser.get(stateName) : null;
        // the definition's shape wins if it names one (e.g. an imported vanilla model); otherwise the family's
        BlockShape shape = def != null && def.shape() != null ? BlockShapeParser.find(def.shape(), s) : null;
        if (shape == null) shape = BlockShapeParser.find(shapeFor(s), s);
        b.setShape(shape)
         // leaves are drawn opaque by default, like vanilla's "fast" leaves: faster (they hide their
         // neighbors' faces) and their see-through pixels show the dark color stored under them;
         // the transparent_leaves option cuts them out instead, like "fancy" leaves
         .setRenderLayer(fastLeaves() ? RenderLayer.OPAQUE : layer)
         .setSolidTexture(fastLeaves())
         .setLeaves(isLeaves())
         .setBaseName(name)
         .setWaterlogged(is(s, "waterlogged") || ALWAYS_IN_WATER.contains(name))
         .setHardness(hardness)
         .setEffectiveTool(tool)
         .setSolid(solidFor(s))
         .setLiquid(liquid)
         .setClimbable(climbable)
         .setFriction(friction)
         .setSpeedFactor(speed)
         .setLightEmission(lightFor(s))
         .setLightOpacity(opacity)
         .setSoundGroup(sound)
         .setDisplayName(displayName(name));
        if (def != null) {
            b.setTextures(def.tex());
            b.setTextureRotation(def.rotation());
            b.setUvVariants(def.uvVariants());
            b.setTintFaces(def.tintFaces());
        }
    }

    // ---- per-state rules ----
    private String shapeFor(Map<String, String> s) {
        if (family.equals("slab")) {
            switch (s.get("type")) {
                case "double": return "full";
                case "top":    return "slab_top";
                default:       return "slab_bottom";
            }
        }
        return family;
    }

    private boolean solidFor(Map<String, String> s) {
        if (!solid) return false;
        if (family.equals("fence_gate") && is(s, "open")) return false;
        if (name.equals("snow") && "1".equals(s.get("layers"))) return false;
        return true;
    }

    private int lightFor(Map<String, String> s) {
        if (name.equals("light")) return Integer.parseInt(s.get("level"));
        if (s.containsKey("candles")) return is(s, "lit") ? 3 * Integer.parseInt(s.get("candles")) : 0;
        if (family.equals("candle_cake")) return is(s, "lit") ? 3 : 0;
        if (name.equals("sea_pickle")) return is(s, "waterlogged") ? 3 + 3 * Integer.parseInt(s.get("pickles")) : 0;
        if (name.equals("respawn_anchor")) {
            int c = Integer.parseInt(s.get("charges"));
            return c == 0 ? 0 : 4 * c - 1;
        }
        if (s.containsKey("berries")) return is(s, "berries") ? light : 0;
        if (s.containsKey("lit")) return is(s, "lit") ? light : 0;
        return light;
    }

    private static boolean is(Map<String, String> s, String key) { return "true".equals(s.get(key)); }

    private boolean isLeaves() { return name.endsWith("_leaves"); }
    private boolean fastLeaves() { return isLeaves() && !Options.TRANSPARENT_LEAVES; }

    /** Blocks that have no waterlogged property but are always filled with water, like in vanilla. */
    private static final java.util.Set<String> ALWAYS_IN_WATER =
            java.util.Set.of("seagrass", "tall_seagrass", "kelp", "kelp_plant", "bubble_column");

    private static String displayName(String id) {
        StringBuilder sb = new StringBuilder(id.length());
        boolean up = true;
        for (char c : id.toCharArray()) {
            if (c == '_') { sb.append(' '); up = true; }
            else { sb.append(up ? Character.toUpperCase(c) : c); up = false; }
        }
        return sb.toString();
    }
}
