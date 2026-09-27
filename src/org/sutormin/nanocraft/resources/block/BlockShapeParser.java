package org.sutormin.nanocraft.resources.block;

import org.sutormin.nanocraft.Main;
import org.sutormin.nanocraft.data.Registries;
import org.sutormin.nanocraft.data.types.BlockShape;
import org.sutormin.nanocraft.world.Direction;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BlockShapeParser {

    /** A shape named with a state tag, e.g. "stairs[facing=north,half=top,shape=straight]". */
    private record Variant(Map<String, String[]> props, BlockShape shape) {
        boolean matches(Map<String, String> state) {
            for (Map.Entry<String, String[]> e : props.entrySet()) {
                String value = state.get(e.getKey());
                if (value == null || !List.of(e.getValue()).contains(value)) return false;
            }
            return true;
        }
    }

    // family name ("stairs") -> its state variants, in file order
    private static final Map<String, List<Variant>> variants = new HashMap<>();

    /**
     * The shape for a block state: the matching variant of {@code family} with the most properties
     * (ties go to the one defined first), otherwise the shape named just {@code family}, or null.
     * State properties a variant doesn't mention (e.g. waterlogged) are ignored.
     */
    public static BlockShape find(String family, Map<String, String> state) {
        BlockShape best = null;
        int bestSize = -1;
        for (Variant v : variants.getOrDefault(family, List.of())) {
            if (v.props().size() > bestSize && v.matches(state)) {
                best = v.shape();
                bestSize = v.props().size();
            }
        }
        return best != null ? best : Registries.BLOCK_SHAPE.get(family);
    }

    private static final String ROOT = "/assets/";
    private static final String PATH = "/assets/indexes/blkmdl.idx";


    public static void loadFromIndex() {
        try (InputStream in = Main.class.getResourceAsStream(PATH)) {

            if (in == null) {
                throw new RuntimeException(
                        "Resource '" + PATH + "' not found"
                );
            }

            String contents = new String(
                    in.readAllBytes(),
                    StandardCharsets.UTF_8
            );

            for (String line : contents.split(";")) {
                line = line.trim();
                line = line.replaceAll("\\R", "");

                if (line.isEmpty()
                        || line.startsWith("//")
                        || line.startsWith("#")) {
                    continue;
                }

                loadFromFile(line);
            }

        } catch (IOException e) {
            throw new RuntimeException(
                    "Failed to load block shape index '" + PATH + "'",
                    e
            );
        }
    }

    public static void loadFromFile(String file) {
        String path = ROOT + file;

        try (InputStream in = Main.class.getResourceAsStream(path)) {

            if (in == null) {
                throw new RuntimeException(
                        "Resource '" + path + "' not found"
                );
            }

            String contents = new String(
                    in.readAllBytes(),
                    StandardCharsets.UTF_8
            );

            for (String line : contents.split(";")) {
                line = line.trim();
                line = line.replaceAll("\\R", "");

                if (line.isEmpty()
                        || line.startsWith("//")
                        || line.startsWith("#")) {
                    continue;
                }

                parse(line);
            }

        } catch (IOException e) {
            throw new RuntimeException(
                    "Failed to load block shape file '" + file + "'",
                    e
            );
        }
    }

    public static BlockShape parse(String str) {
        String[] sections = str.split("\\|", -1);

        if (sections.length != 3) {
            throw new RuntimeException(
                    "Invalid block shape, expected 3 sections: " + str
            );
        }

        String name = sections[0].trim();

        if (name.isEmpty()) {
            throw new RuntimeException(
                    "Block shape has no name"
            );
        }

        BlockShape shape = Registries.BLOCK_SHAPE.addNew(name);

        int bracket = name.indexOf('[');
        if (bracket >= 0) {
            Map<String, String[]> props = new HashMap<>();
            String inner = name.substring(bracket + 1, name.length() - 1);
            for (String kv : inner.isEmpty() ? new String[0] : inner.split(",")) {
                int eq = kv.indexOf('=');
                if (eq < 0) throw new RuntimeException("Invalid state property '" + kv + "' in shape '" + name + "'");
                props.put(kv.substring(0, eq), kv.substring(eq + 1).split("\\|"));
            }
            variants.computeIfAbsent(name.substring(0, bracket), k -> new ArrayList<>())
                    .add(new Variant(props, shape));
        }

        parseVertices(shape, sections[1], name);
        parseFaces(shape, sections[2], name);

        return shape;
    }

    private static void parseVertices(
            BlockShape shape,
            String str,
            String shapeName
    ) {
        if (str.isBlank()) {
            return;
        }

        String[] vertices = str.trim().split("\\s+");

        for (int i = 0; i < vertices.length; i++) {
            String[] data = vertices[i].split(",");

            if (data.length != 3) {
                throw new RuntimeException(
                        "Invalid vertex " + i +
                                " in shape '" + shapeName + "': " +
                                vertices[i]
                );
            }

            try {
                shape.addVertex(
                        Float.parseFloat(data[0]),
                        Float.parseFloat(data[1]),
                        Float.parseFloat(data[2])
                );
            } catch (NumberFormatException e) {
                throw new RuntimeException(
                        "Invalid vertex " + i +
                                " in shape '" + shapeName + "': " +
                                vertices[i],
                        e
                );
            }
        }
    }

    private static void parseFaces(
            BlockShape shape,
            String str,
            String shapeName
    ) {
        if (str.isBlank()) {
            return;
        }

        String[] faces = str.trim().split("\\s+");

        for (int i = 0; i < faces.length; i++) {
            parseFace(
                    shape,
                    faces[i],
                    shapeName,
                    i
            );
        }
    }

    private static void parseFace(
            BlockShape shape,
            String str,
            String shapeName,
            int faceIndex
    ) {
        String[] data = str.split(",");

        if (data.length < 11) {
            throw new RuntimeException(
                    "Invalid face " + faceIndex +
                            " in shape '" + shapeName + "': " +
                            str
            );
        }

        Direction direction;

        try {
            direction = Direction.valueOf(
                    data[0].toUpperCase()
            );
        } catch (IllegalArgumentException e) {
            throw new RuntimeException(
                    "Invalid direction '" + data[0] +
                            "' in face " + faceIndex +
                            " of shape '" + shapeName + "'",
                    e
            );
        }

        boolean shouldCull;

        if (data[1].equalsIgnoreCase("true")) {
            shouldCull = true;
        } else if (data[1].equalsIgnoreCase("false")) {
            shouldCull = false;
        } else {
            throw new RuntimeException(
                    "Invalid cull value '" + data[1] +
                            "' in face " + faceIndex +
                            " of shape '" + shapeName + "'"
            );
        }

        int remaining = data.length - 2;

        if (remaining % 3 != 0) {
            throw new RuntimeException(
                    "Incomplete vertex/UV data in face " +
                            faceIndex +
                            " of shape '" +
                            shapeName +
                            "': " +
                            str
            );
        }

        int vertexCount = remaining / 3;

        if (vertexCount < 3) {
            throw new RuntimeException(
                    "Face " + faceIndex +
                            " in shape '" + shapeName +
                            "' has fewer than 3 vertices"
            );
        }

        int[] vertices = new int[vertexCount];
        float[][] uv = new float[vertexCount][2];

        for (int i = 0; i < vertexCount; i++) {
            int offset = 2 + i * 3;

            try {
                int vertex = Integer.parseInt(data[offset]);

                if (vertex < 0 ||
                        vertex >= shape.getVertices().size()) {
                    throw new RuntimeException(
                            "Vertex index " + vertex +
                                    " is out of bounds in face " +
                                    faceIndex +
                                    " of shape '" +
                                    shapeName +
                                    "'"
                    );
                }

                vertices[i] = vertex;

                uv[i][0] = Float.parseFloat(data[offset + 1]);
                uv[i][1] = Float.parseFloat(data[offset + 2]);

            } catch (NumberFormatException e) {
                throw new RuntimeException(
                        "Invalid vertex/UV data in face " +
                                faceIndex +
                                " of shape '" +
                                shapeName +
                                "': " +
                                str,
                        e
                );
            }
        }

        shape.addFace(
                vertices,
                direction,
                shouldCull,
                uv
        );
    }
}