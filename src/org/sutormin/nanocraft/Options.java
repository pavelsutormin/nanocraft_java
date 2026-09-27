package org.sutormin.nanocraft;

import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.constructor.SafeConstructor;
import org.yaml.snakeyaml.nodes.NodeId;
import org.yaml.snakeyaml.nodes.Tag;
import org.yaml.snakeyaml.representer.Representer;
import org.yaml.snakeyaml.resolver.Resolver;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Settings, read from options.yaml in the working directory at startup ({@link #load}).
 *
 * <p>The file starts with {@code CONFIG_VERSION}, the layout it was written in. A file from an older
 * version is migrated: its values are carried over to the current layout, the old file is kept as a
 * backup and a new one is written. Version history:
 * <ol>
 *   <li>options.txt: flat {@code key=value} lines with snake_case keys</li>
 *   <li>options.yaml: sections (SERVER, PLAYER, GRAPHICS, PERFORMANCE, DEBUG) with UPPER_CASE keys</li>
 * </ol>
 * To change the layout (rename, move or remove options), bump {@link #CONFIG_VERSION}, change
 * {@link #SECTIONS}, and add a step to {@link #MIGRATIONS} that turns the previous version's values
 * into the new ones. Adding an option needs no new version: a file without it gets it filled in with
 * its default ({@link #load}).
 */
public class Options {
    public static String SERVER_IP = "127.0.0.1";
    public static int PORT = 25565;

    public static String PLAYER_USERNAME = "Player";
    /** Offline-mode UUID, derived from the username the way vanilla servers do. */
    public static UUID PLAYER_UUID = offlineUuid(PLAYER_USERNAME);
    public static byte VIEW_DISTANCE = 10;
    /**
     * Chunks per tick the server should send, sent after each chunk batch. Vanilla servers clamp it
     * to 0.01-64 (they start at 9); vanilla clients send a measured rate instead of a fixed one.
     */
    public static float RECEIVE_CHUNKS_PER_TICK = 5;

    /** Background threads that build chunk meshes. */
    public static int MESH_THREADS = Math.clamp(Runtime.getRuntime().availableProcessors() - 1, 1, 8);
    /** Max time per frame spent uploading finished chunk meshes to the GPU, in milliseconds. */
    public static float MESH_UPLOAD_BUDGET_MS = 4.0f;

    /** See-through leaves, like vanilla's "fancy" leaves; off draws them opaque ("fast"), which is quicker. */
    public static boolean TRANSPARENT_LEAVES = false;

    // Debugging
    /** Prints every server packet NanoCraft doesn't handle (they're skipped). */
    public static boolean DEBUG_LOG_UNKNOWN_S2C_PACKETS = false;
    /** Prints every server packet NanoCraft handles. Chunk packets make this busy. */
    public static boolean DEBUG_LOG_KNOWN_S2C_PACKETS = false;
    /** Prints every packet sent to the server. Positions and tick ends go out 20 times a second. */
    public static boolean DEBUG_LOG_C2S_PACKETS = false;
    /** Prints the name of each texture that isn't found (they show the null.png fallback). */
    public static boolean DEBUG_LOG_MISSING_TEXTURES = false;
    /** Shows FPS, loaded chunks and position in the window title. */
    public static boolean DEBUG_SHOW_FPS = false;
    /** Draws the world as wireframe triangles. */
    public static boolean DEBUG_WIREFRAME = false;

    // ------------------------------------------------------------------
    // File layout
    // ------------------------------------------------------------------

    /** The layout this version writes; see the class docs for the history. */
    public static final int CONFIG_VERSION = 2;

    private enum Kind { STRING, INT, FLOAT, BOOL }

    private record Setting(String key, String comment, Kind kind, double min, double max,
                           Supplier<Object> get, Consumer<Object> set) {}

    private record Section(String name, String comment, List<Setting> settings) {}

    private static Setting text(String key, String comment, Supplier<Object> get, Consumer<Object> set) {
        return new Setting(key, comment, Kind.STRING, 0, 0, get, set);
    }

    private static Setting integer(String key, String comment, int min, int max,
                                   Supplier<Object> get, Consumer<Object> set) {
        return new Setting(key, comment, Kind.INT, min, max, get, set);
    }

    private static Setting decimal(String key, String comment, double min, double max,
                                   Supplier<Object> get, Consumer<Object> set) {
        return new Setting(key, comment, Kind.FLOAT, min, max, get, set);
    }

    private static Setting flag(String key, String comment, Supplier<Object> get, Consumer<Object> set) {
        return new Setting(key, comment, Kind.BOOL, 0, 0, get, set);
    }

    /** The current layout: sections of settings, in file order. */
    private static final List<Section> SECTIONS = List.of(
            new Section("SERVER", "the server to join: a Minecraft 26.3 server in offline mode", List.of(
                    text("ADDRESS", null, () -> SERVER_IP, v -> SERVER_IP = (String) v),
                    integer("PORT", null, 1, 65535, () -> PORT, v -> PORT = (Integer) v))),
            new Section("PLAYER", null, List.of(
                    text("USERNAME", "your name on the server: 3-16 letters, digits or _",
                            () -> PLAYER_USERNAME, v -> PLAYER_USERNAME = (String) v))),
            new Section("GRAPHICS", null, List.of(
                    integer("VIEW_DISTANCE", "chunks the server sends around you (2-32)", 2, 32,
                            () -> (int) VIEW_DISTANCE, v -> VIEW_DISTANCE = (byte) (int) (Integer) v),
                    flag("TRANSPARENT_LEAVES",
                            "see-through leaves like vanilla's \"fancy\" leaves (false: opaque \"fast\" leaves, quicker)",
                            () -> TRANSPARENT_LEAVES, v -> TRANSPARENT_LEAVES = (Boolean) v))),
            new Section("PERFORMANCE", null, List.of(
                    decimal("CHUNKS_PER_TICK", "chunks per tick the server should send you (0.01-64); higher loads faster, but costs more per frame",
                            0.01, 64, () -> RECEIVE_CHUNKS_PER_TICK, v -> RECEIVE_CHUNKS_PER_TICK = ((Double) v).floatValue()),
                    integer("MESH_THREADS", "background threads that build chunk meshes (default: CPU cores - 1, at most 8)",
                            1, 64, () -> MESH_THREADS, v -> MESH_THREADS = (Integer) v),
                    decimal("MESH_UPLOAD_BUDGET_MS", "max milliseconds per frame spent uploading chunk meshes to the GPU",
                            0.1, 1000, () -> MESH_UPLOAD_BUDGET_MS, v -> MESH_UPLOAD_BUDGET_MS = ((Double) v).floatValue()))),
            new Section("DEBUG", "debugging (true/false)", List.of(
                    flag("LOG_UNKNOWN_S2C_PACKETS", "print server packets NanoCraft skips",
                            () -> DEBUG_LOG_UNKNOWN_S2C_PACKETS, v -> DEBUG_LOG_UNKNOWN_S2C_PACKETS = (Boolean) v),
                    flag("LOG_KNOWN_S2C_PACKETS", "print server packets NanoCraft handles (chunks make this busy)",
                            () -> DEBUG_LOG_KNOWN_S2C_PACKETS, v -> DEBUG_LOG_KNOWN_S2C_PACKETS = (Boolean) v),
                    flag("LOG_C2S_PACKETS", "print packets sent to the server (20 a second while playing)",
                            () -> DEBUG_LOG_C2S_PACKETS, v -> DEBUG_LOG_C2S_PACKETS = (Boolean) v),
                    flag("LOG_MISSING_TEXTURES", "print each texture that isn't found, instead of just how many",
                            () -> DEBUG_LOG_MISSING_TEXTURES, v -> DEBUG_LOG_MISSING_TEXTURES = (Boolean) v),
                    flag("SHOW_FPS", "FPS, loaded chunks and position in the window title",
                            () -> DEBUG_SHOW_FPS, v -> DEBUG_SHOW_FPS = (Boolean) v),
                    flag("WIREFRAME", "draw the world as wireframe",
                            () -> DEBUG_WIREFRAME, v -> DEBUG_WIREFRAME = (Boolean) v))));

    /**
     * Migration steps: the step for version n turns version n's values into version n + 1's. Values
     * are the nested maps read from the file (for version 1, one flat map read from options.txt).
     */
    private static final Map<Integer, Function<Map<String, Object>, Map<String, Object>>> MIGRATIONS = Map.of(
            1, Options::migrate1To2);

    /** options.txt to options.yaml: snake_case keys become UPPER_CASE keys in sections. */
    private static Map<String, Object> migrate1To2(Map<String, Object> old) {
        Map<String, String> renamed = Map.ofEntries(
                Map.entry("server", "SERVER.ADDRESS"),
                Map.entry("port", "SERVER.PORT"),
                Map.entry("username", "PLAYER.USERNAME"),
                Map.entry("view_distance", "GRAPHICS.VIEW_DISTANCE"),
                Map.entry("transparent_leaves", "GRAPHICS.TRANSPARENT_LEAVES"),
                Map.entry("mesh_threads", "PERFORMANCE.MESH_THREADS"),
                Map.entry("mesh_upload_budget_ms", "PERFORMANCE.MESH_UPLOAD_BUDGET_MS"),
                Map.entry("debug_log_unknown_s2c_packets", "DEBUG.LOG_UNKNOWN_S2C_PACKETS"),
                Map.entry("debug_log_known_s2c_packets", "DEBUG.LOG_KNOWN_S2C_PACKETS"),
                Map.entry("debug_log_c2s_packets", "DEBUG.LOG_C2S_PACKETS"),
                Map.entry("debug_log_missing_textures", "DEBUG.LOG_MISSING_TEXTURES"),
                Map.entry("debug_show_fps", "DEBUG.SHOW_FPS"),
                Map.entry("debug_wireframe", "DEBUG.WIREFRAME"));
        Map<String, Object> out = new LinkedHashMap<>();
        for (Map.Entry<String, Object> e : old.entrySet()) {
            // unknown old keys stay as they are, so they get reported like any unknown key
            putPath(out, renamed.getOrDefault(e.getKey(), e.getKey()), e.getValue());
        }
        return out;
    }

    // ------------------------------------------------------------------
    // Loading
    // ------------------------------------------------------------------

    /**
     * Reads the options file. Writes one with the defaults if there's none, migrates an older one
     * (including an options.txt next to it, from before options.yaml), and fills in options missing from
     * a current one (added since it was written), keeping the file it replaces as a backup.
     */
    public static void load(Path file) {
        Path legacy = file.resolveSibling("options.txt");
        Path source;
        Map<String, Object> values;
        int version;
        boolean versionMissing = false;

        if (Files.exists(file)) {
            Object root;
            try {
                root = yaml().load(Files.readString(file, StandardCharsets.UTF_8));
            } catch (Exception e) {
                // leave a file with a typo alone, so it can be fixed; run with the defaults meanwhile
                System.err.println("[Client] Couldn't read " + file + ", using the defaults: " + e.getMessage());
                return;
            }
            if (root == null) root = Map.of(); // empty file
            if (!(root instanceof Map<?, ?> map)) {
                System.err.println("[Client] " + file + " isn't a list of settings; using the defaults");
                return;
            }
            values = stringKeys(map);
            if (values.remove("CONFIG_VERSION") instanceof Integer n) {
                version = n;
            } else {
                System.err.println("[Client] " + file + " has no CONFIG_VERSION; reading it as version " + CONFIG_VERSION);
                version = CONFIG_VERSION;
                versionMissing = true;
            }
            source = file;
        } else if (Files.exists(legacy)) {
            values = readLegacy(legacy);
            version = 1;
            source = legacy;
        } else {
            write(file);
            System.out.println("[Client] Wrote default options to " + file.toAbsolutePath());
            return;
        }

        if (version > CONFIG_VERSION) {
            System.err.println("[Client] " + file + " is from a newer NanoCraft (CONFIG_VERSION " + version
                    + ", this one knows up to " + CONFIG_VERSION + "); reading what it can and leaving the file alone");
        }
        int from = version;
        for (; version < CONFIG_VERSION; version++) {
            var step = MIGRATIONS.get(version);
            if (step == null) {
                System.err.println("[Client] Can't migrate options from CONFIG_VERSION " + version
                        + "; using the defaults and leaving " + source + " alone");
                return;
            }
            values = step.apply(values);
        }

        Applied applied = apply(source, values);

        if (from < CONFIG_VERSION) {
            // keep the old file next to the new one: options.txt.bak, or e.g. options.yaml.v2.bak
            Path backup = source.equals(file)
                    ? file.resolveSibling(file.getFileName() + ".v" + from + ".bak")
                    : source.resolveSibling(source.getFileName() + ".bak");
            replace(source, backup, file, "Migrated options from " + source + " (CONFIG_VERSION " + from + ") to "
                    + file + " (CONFIG_VERSION " + CONFIG_VERSION + ")");
        } else if (from == CONFIG_VERSION && (versionMissing || !applied.missing().isEmpty())) {
            if (applied.problems()) {
                // someone is editing it: rewriting would replace their bad values with defaults
                System.err.println("[Client] " + file + " is missing " + missingText(applied.missing(), versionMissing)
                        + ", but isn't filled in until the problems above are fixed");
            } else {
                replace(file, file.resolveSibling(file.getFileName() + ".bak"), file,
                        "Added " + missingText(applied.missing(), versionMissing) + " to " + file);
            }
        }
    }

    /** Moves {@code old} to {@code backup} and writes the current options to {@code file}. */
    private static void replace(Path old, Path backup, Path file, String message) {
        try {
            Files.move(old, backup, StandardCopyOption.REPLACE_EXISTING);
            write(file);
            System.out.println("[Client] " + message + "; the old file is kept as " + backup);
        } catch (IOException e) {
            System.err.println("[Client] Couldn't rewrite " + file + ": " + e);
        }
    }

    private static String missingText(List<String> missing, boolean versionMissing) {
        List<String> all = new ArrayList<>(missing);
        if (versionMissing) all.addFirst("CONFIG_VERSION");
        return (all.size() == 1 ? "option " : all.size() + " options: ") + String.join(", ", all);
    }

    /**
     * What {@link #apply} found: the options missing from the file ("SECTION.KEY"), and whether any
     * value was bad or any key unknown.
     */
    private record Applied(List<String> missing, boolean problems) {}

    /** Sets every known option from the values, warning about unknown keys and bad values. */
    private static Applied apply(Path file, Map<String, Object> values) {
        List<String> missing = new ArrayList<>();
        boolean problems = false;
        for (Section section : SECTIONS) {
            Object raw = values.remove(section.name());
            if (raw != null && !(raw instanceof Map<?, ?>)) {
                System.err.println("[Client] " + file + ": \"" + section.name() + "\" should be a section of settings");
                problems = true;
                continue;
            }
            Map<String, Object> entries = raw == null ? new LinkedHashMap<>() : stringKeys((Map<?, ?>) raw);
            for (Setting setting : section.settings()) {
                String name = section.name() + "." + setting.key();
                if (!entries.containsKey(setting.key())) {
                    missing.add(name); // keeps its default
                    continue;
                }
                Object value = convert(setting, entries.remove(setting.key()));
                if (value == null) {
                    System.err.println("[Client] " + file + ": " + name + " " + describe(setting) + "; keeping the default");
                    problems = true;
                } else {
                    setting.set().accept(value);
                }
            }
            for (String key : entries.keySet()) {
                System.err.println("[Client] " + file + ": unknown option \"" + section.name() + "." + key + "\"");
                problems = true;
            }
        }
        for (String key : values.keySet()) {
            System.err.println("[Client] " + file + ": unknown option \"" + key + "\"");
            problems = true;
        }

        if (!PLAYER_USERNAME.matches("[A-Za-z0-9_]{3,16}")) {
            System.err.println("[Client] Username \"" + PLAYER_USERNAME + "\" isn't 3-16 letters, digits or _;"
                    + " servers will probably refuse it");
        }
        PLAYER_UUID = offlineUuid(PLAYER_USERNAME);
        return new Applied(missing, problems);
    }

    /**
     * A file value as the setting's type, or null if it doesn't fit. Text is accepted for every type,
     * since options.txt values (and quoted YAML values) arrive as text.
     */
    private static Object convert(Setting setting, Object value) {
        if (value == null) return null;
        String text = value.toString().strip();
        try {
            return switch (setting.kind()) {
                case STRING -> text; // a username like true or 123 is still a name
                case INT -> {
                    int n = value instanceof Integer i ? i : Integer.parseInt(text);
                    yield n >= setting.min() && n <= setting.max() ? n : null;
                }
                case FLOAT -> {
                    double d = value instanceof Number number ? number.doubleValue() : Double.parseDouble(text);
                    yield d >= setting.min() && d <= setting.max() ? d : null;
                }
                case BOOL -> value instanceof Boolean b ? b
                        : text.equalsIgnoreCase("true") ? Boolean.TRUE
                        : text.equalsIgnoreCase("false") ? Boolean.FALSE
                        : null;
            };
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private static String describe(Setting setting) {
        return switch (setting.kind()) {
            case STRING -> "needs text";
            case INT -> "needs a whole number from " + (int) setting.min() + " to " + (int) setting.max();
            case FLOAT -> "needs a number from " + setting.min() + " to " + setting.max();
            case BOOL -> "needs true or false";
        };
    }

    /** options.txt (CONFIG_VERSION 1): key=value lines, # comments. */
    private static Map<String, Object> readLegacy(Path file) {
        Map<String, Object> values = new LinkedHashMap<>();
        try {
            for (String line : Files.readAllLines(file, StandardCharsets.UTF_8)) {
                line = line.strip();
                if (line.isEmpty() || line.startsWith("#")) continue;
                int eq = line.indexOf('=');
                if (eq < 0) {
                    System.err.println("[Client] " + file + ": ignoring \"" + line + "\" (expected key=value)");
                    continue;
                }
                values.put(line.substring(0, eq).strip(), line.substring(eq + 1).strip());
            }
        } catch (IOException e) {
            System.err.println("[Client] Couldn't read " + file + ": " + e);
        }
        return values;
    }

    // ------------------------------------------------------------------
    // Writing
    // ------------------------------------------------------------------

    /** Writes every option with its current value in the current layout, with comments. */
    private static void write(Path file) {
        StringBuilder out = new StringBuilder();
        out.append("# NanoCraft options.\n");
        out.append("# CONFIG_VERSION is the layout of this file; NanoCraft uses it to migrate older files, so leave it be.\n");
        out.append("CONFIG_VERSION: ").append(CONFIG_VERSION).append('\n');
        for (Section section : SECTIONS) {
            out.append('\n');
            if (section.comment() != null) out.append("# ").append(section.comment()).append('\n');
            out.append(section.name()).append(":\n");
            for (Setting setting : section.settings()) {
                if (setting.comment() != null) out.append("  # ").append(setting.comment()).append('\n');
                out.append("  ").append(setting.key()).append(": ").append(yamlValue(setting)).append('\n');
            }
        }
        try {
            Files.writeString(file, out);
        } catch (IOException e) {
            System.err.println("[Client] Couldn't write " + file + ": " + e);
        }
    }

    private static String yamlValue(Setting setting) {
        Object value = setting.get().get();
        if (setting.kind() != Kind.STRING) return value.toString();
        // double-quoted, so text like yes, 123 or a:b stays text
        return "\"" + value.toString().replace("\\", "\\\\").replace("\"", "\\\"") + "\"";
    }

    // ------------------------------------------------------------------
    // Helpers
    // ------------------------------------------------------------------

    /**
     * A YAML reader that only makes plain objects (no Java classes), and that reads booleans the YAML
     * 1.2 way: only true and false. SnakeYAML follows YAML 1.1 by default, where yes, no, on and off
     * are booleans too, which would turn a username like "yes" into "true".
     */
    private static Yaml yaml() {
        Resolver strictBooleans = new Resolver() {
            @Override
            public Tag resolve(NodeId kind, String value, boolean implicit) {
                Tag tag = super.resolve(kind, value, implicit);
                if (tag.equals(Tag.BOOL) && !value.equalsIgnoreCase("true") && !value.equalsIgnoreCase("false")) {
                    return Tag.STR;
                }
                return tag;
            }
        };
        DumperOptions dumperOptions = new DumperOptions();
        LoaderOptions loaderOptions = new LoaderOptions();
        return new Yaml(new SafeConstructor(loaderOptions), new Representer(dumperOptions), dumperOptions,
                loaderOptions, strictBooleans);
    }

    private static Map<String, Object> stringKeys(Map<?, ?> map) {
        Map<String, Object> out = new LinkedHashMap<>();
        for (Map.Entry<?, ?> e : map.entrySet()) out.put(String.valueOf(e.getKey()), e.getValue());
        return out;
    }

    /** Puts a value at a dotted path ("SERVER.PORT"), creating the sections on the way. */
    @SuppressWarnings("unchecked") // the sections are only ever maps made here
    private static void putPath(Map<String, Object> map, String path, Object value) {
        String[] parts = path.split("\\.");
        Map<String, Object> current = map;
        for (int i = 0; i < parts.length - 1; i++) {
            current = (Map<String, Object>) current.computeIfAbsent(parts[i], k -> new LinkedHashMap<String, Object>());
        }
        current.put(parts[parts.length - 1], value);
    }

    private static UUID offlineUuid(String username) {
        return UUID.nameUUIDFromBytes(("OfflinePlayer:" + username).getBytes(StandardCharsets.UTF_8));
    }
}
