package org.sutormin.nanocraft.resources.block;

import org.sutormin.nanocraft.Main;
import org.sutormin.nanocraft.data.types.Block;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BlockDefinitionParser {
    /**
     * One .def line: {@code <name> <shape> <textures...> [options]}. {@code shape} is null for blocks that
     * fall back to @default (they keep their family's shape). Options are key=value tokens
     * after the textures: {@code rotate=random|random_all|mirror}, or {@code uv=...} (see
     * {@link #parseUvVariants}), and {@code tint=<face>,<face>,...|all}: faces colored by the biome's
     * grass color. {@code uvVariants} and {@code tintFaces} are null unless the line has that option;
     * an empty {@code tintFaces} means every face.
     */
    public record BlockDefinition(String shape, String[] tex, Block.TextureRotation rotation, int[][] uvVariants,
                                  int[] tintFaces){}

    /**
     * Parses {@code uv=<t>,<t>,.../<t>,<t>,...}: one transform per face in the shape's face order,
     * with {@code /} separating variants that are picked at random per block. A transform is
     * r0, r90, r180 or r270 (turn the texture), optionally prefixed with m (mirror it first).
     * Returned as [variant][face] codes: bit 2 = mirror, bits 0-1 = quarter turns.
     */
    private static int[][] parseUvVariants(String value, String line) {
        String[] variants = value.split("/");
        int[][] out = new int[variants.length][];
        for (int v = 0; v < variants.length; v++) {
            String[] faces = variants[v].split(",");
            out[v] = new int[faces.length];
            for (int f = 0; f < faces.length; f++) {
                String t = faces[f];
                boolean mirror = t.startsWith("m");
                String turn = mirror ? t.substring(1) : t;
                int turns = switch (turn) {
                    case "r0" -> 0;
                    case "r90" -> 1;
                    case "r180" -> 2;
                    case "r270" -> 3;
                    default -> throw new RuntimeException("Invalid uv transform '" + t + "' in: " + line);
                };
                out[v][f] = (mirror ? 4 : 0) | turns;
            }
        }
        return out;
    }

    /**
     * A definition for some states of a block, e.g. "oak_door[half=upper]" or "carrots[age=0|1]".
     * Matches any state whose listed properties have one of the listed values; other properties are ignored.
     */
    private record StatePattern(Map<String, String[]> props, BlockDefinition def){
        boolean matches(Map<String, String> state) {
            for (Map.Entry<String, String[]> e : props.entrySet()) {
                String value = state.get(e.getKey());
                if (value == null || !Arrays.asList(e.getValue()).contains(value)) return false;
            }
            return true;
        }
    }

    // definitions without a state tag, keyed by block name (and "@default")
    private static final Map<String, BlockDefinition> defs = new HashMap<>();
    // definitions with a state tag, keyed by base block name, in file order
    private static final Map<String, List<StatePattern>> patterns = new HashMap<>();
    public static void loadFromIndex(){
        try (InputStream in = Main.class.getResourceAsStream(
                "/assets/indexes/def.idx")) {

            if (in == null) {
                throw new RuntimeException("Resource /assets/indexes/def.idx not found");
            }

            String strs = new String(in.readAllBytes(), StandardCharsets.UTF_8);
            for (String str : strs.split("\\R")) {
                str = str.trim();

                if (str.isEmpty()) {continue;}

                if (str.startsWith("//")){continue;}

                if (str.startsWith("#")){continue;}

                loadFromFile(str);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public static void loadFromFile(String file){
        try (InputStream in = Main.class.getResourceAsStream(
                "/assets/"+file)) {

            if (in == null) {
                throw new RuntimeException("Resource not found");
            }

            String strs = new String(in.readAllBytes(), StandardCharsets.UTF_8);
            for (String str : strs.split("\\R")) {
                str = str.trim();

                if (str.isEmpty() || str.startsWith("//") || str.startsWith("#")) {
                    continue;
                }

                String[] data = str.split("\\s+");

                List<String> textures = new ArrayList<>();
                Block.TextureRotation rotation = Block.TextureRotation.NONE;
                int[][] uvVariants = null;
                int[] tintFaces = null;
                for (String token : Arrays.copyOfRange(data, 2, data.length)) {
                    if (!token.contains("=")) {
                        textures.add(token);
                    } else if (token.equals("rotate=random")) {
                        rotation = Block.TextureRotation.RANDOM_TOP_BOTTOM;
                    } else if (token.equals("rotate=random_all")) {
                        rotation = Block.TextureRotation.RANDOM_ALL;
                    } else if (token.equals("rotate=mirror")) {
                        rotation = Block.TextureRotation.RANDOM_MIRROR;
                    } else if (token.startsWith("uv=")) {
                        uvVariants = parseUvVariants(token.substring(3), str);
                    } else if (token.startsWith("tint=")) {
                        String faces = token.substring(5);
                        tintFaces = faces.equals("all") ? new int[0]
                                : Arrays.stream(faces.split(",")).mapToInt(Integer::parseInt).toArray();
                    } else {
                        throw new RuntimeException("Unknown option '" + token + "' in " + file + ": " + str);
                    }
                }

                if (uvVariants != null && rotation != Block.TextureRotation.NONE) {
                    throw new RuntimeException("Use either rotate= or uv=, not both, in " + file + ": " + str);
                }
                BlockDefinition def = new BlockDefinition(data[1], textures.toArray(new String[0]), rotation, uvVariants,
                        tintFaces);

                int bracket = data[0].indexOf('[');
                if (bracket < 0) {
                    defs.put(data[0], def);
                } else {
                    Map<String, String[]> props = new HashMap<>();
                    for (Map.Entry<String, String> e : parseState(data[0].substring(bracket)).entrySet()) {
                        props.put(e.getKey(), e.getValue().split("\\|"));
                    }
                    patterns.computeIfAbsent(data[0].substring(0, bracket), k -> new ArrayList<>())
                            .add(new StatePattern(props, def));
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    /** "[facing=north,half=upper]" -> {facing=north, half=upper} */
    private static Map<String, String> parseState(String tag) {
        Map<String, String> state = new HashMap<>();
        String inner = tag.substring(1, tag.length() - 1);
        if (inner.isEmpty()) return state;
        for (String kv : inner.split(",")) {
            int eq = kv.indexOf('=');
            if (eq < 0) throw new RuntimeException("Invalid block state property '" + kv + "' in " + tag);
            state.put(kv.substring(0, eq), kv.substring(eq + 1));
        }
        return state;
    }

    /**
     * Looks up the definition for a block state, e.g. "oak_door[facing=north,half=upper,...]":
     * the matching state pattern with the most properties ("oak_door[half=upper]"; ties go to the
     * one defined first), then the base block name ("oak_door"), then @default.
     */
    public static BlockDefinition get(String name){
        // * = base block name ("grass_block"), ^ = state tag ("[snowy=true]", or "" if stateless)
        int bracket = name.indexOf('[');
        String base = bracket < 0 ? name : name.substring(0, bracket);
        String state = bracket < 0 ? "" : name.substring(bracket);

        BlockDefinition d = null;
        List<StatePattern> candidates = patterns.get(base);
        if (candidates != null && bracket >= 0) {
            Map<String, String> props = parseState(state);
            int best = -1;
            for (StatePattern p : candidates) {
                if (p.props().size() > best && p.matches(props)) {
                    d = p.def();
                    best = p.props().size();
                }
            }
        }
        if (d == null) d = defs.get(base);
        boolean isDefault = false;
        if (d == null) {
            d = defs.get("@default");
            isDefault = true;
        }
        if (d == null) {
            throw new RuntimeException(
                    "Block definition '" + name + "' not found and no @default is defined"
            );
        }

        // {prop} = that property's value in this state, e.g. redstone_dust_dot_{power}
        Map<String, String> stateProps = bracket < 0 ? Map.of() : parseState(state);
        // @default's shape is only a placeholder: blocks without a definition keep their family's shape
        String newShape = isDefault ? null : fillIn(d.shape, base, state, stateProps);
        String[] newTex = new String[d.tex.length];
        for (int i = 0; i < d.tex.length; i++) {
            newTex[i] = fillIn(d.tex[i], base, state, stateProps);
        }
        return new BlockDefinition(newShape, newTex, d.rotation(), d.uvVariants(), d.tintFaces());
    }
    private static String fillIn(String s, String base, String state, Map<String, String> props) {
        s = s.replace("*", base).replace("^", state);
        for (Map.Entry<String, String> e : props.entrySet()) {
            s = s.replace("{" + e.getKey() + "}", e.getValue());
        }
        return s;
    }

    public static String[] getTexture(String name){
        return get(name).tex();
    }
}
