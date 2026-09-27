import java.security.MessageDigest

plugins {
    java
}

repositories {
    mavenCentral()
}

group = "org.sutormin.nanocraft"
version = "alpha-1.0.1"

val lwjglVersion = "3.4.0"
val lwjglNatives = "natives-linux"

sourceSets {
    main {
        java.setSrcDirs(listOf("src"))
        resources.setSrcDirs(listOf("src/resources"))
    }
}

dependencies {
    implementation("org.lwjgl:lwjgl:$lwjglVersion")
    runtimeOnly("org.lwjgl:lwjgl:$lwjglVersion:$lwjglNatives")
    implementation("org.lwjgl:lwjgl-glfw:$lwjglVersion")
    runtimeOnly("org.lwjgl:lwjgl-glfw:$lwjglVersion:$lwjglNatives")
    implementation("org.lwjgl:lwjgl-opengl:$lwjglVersion")
    runtimeOnly("org.lwjgl:lwjgl-opengl:$lwjglVersion:$lwjglNatives")
    implementation("org.lwjgl:lwjgl-stb:$lwjglVersion")
    runtimeOnly("org.lwjgl:lwjgl-stb:$lwjglVersion:$lwjglNatives")
    implementation("org.joml:joml:1.10.8")
    implementation("io.netty:netty-all:4.2.5.Final")
    implementation("org.yaml:snakeyaml:2.7")
}

tasks.withType<JavaCompile> {
    options.compilerArgs.addAll(
        listOf(
            "-parameters",
            "-Xlint:all",
        )
    )
}

tasks.jar {
    manifest {
        attributes["Main-Class"] = "org.sutormin.nanocraft.Main"
    }

    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    from(configurations.runtimeClasspath.get().map { if (it.isDirectory) it else zipTree(it) }) {
        exclude("META-INF/*.SF")
        exclude("META-INF/*.DSA")
        exclude("META-INF/*.RSA")
    }
}

tasks.register<JavaExec>("runJar") {
    group = "application"
    description = "Builds and runs the NanoCraft jar (settings in options.txt)."

    val jarTask = tasks.named<org.gradle.jvm.tasks.Jar>("jar").get()
    dependsOn(jarTask)

    classpath(jarTask.archiveFile)
}

// ---------------------------------------------------------------------------
// Minecraft downloads for development (mojang/ is gitignored)
// ---------------------------------------------------------------------------

val minecraftVersion = "26.3"
val mojangDir = layout.projectDirectory.dir("mojang")
val clientDir = mojangDir.dir("client")
val serverDir = mojangDir.dir("server")
val clientJar = clientDir.file("client-$minecraftVersion.jar")
val serverJar = serverDir.file("server-$minecraftVersion.jar")

/** Downloads the client or server jar of [minecraftVersion] from Mojang and checks its SHA-1. */
fun downloadMinecraft(kind: String, target: File) {
    val slurper = groovy.json.JsonSlurper()
    val manifest = slurper.parse(uri("https://piston-meta.mojang.com/mc/game/version_manifest_v2.json").toURL()) as Map<*, *>
    val version = (manifest["versions"] as List<*>).map { it as Map<*, *> }.firstOrNull { it["id"] == minecraftVersion }
        ?: throw GradleException("Minecraft $minecraftVersion isn't in Mojang's version manifest")
    val download = ((slurper.parse(uri(version["url"] as String).toURL()) as Map<*, *>)["downloads"] as Map<*, *>)[kind] as Map<*, *>

    target.parentFile.mkdirs()
    val partial = File(target.path + ".part")
    logger.lifecycle("Downloading Minecraft $minecraftVersion $kind jar to ${target.relativeTo(projectDir)}")
    uri(download["url"] as String).toURL().openStream().use { input -> partial.outputStream().use { input.copyTo(it) } }

    val sha1 = MessageDigest.getInstance("SHA-1").digest(partial.readBytes()).joinToString("") { "%02x".format(it) }
    if (sha1 != download["sha1"]) {
        partial.delete()
        throw GradleException("SHA-1 mismatch for the $kind jar: got $sha1, expected ${download["sha1"]}")
    }
    partial.renameTo(target)
}

val downloadClientJar = tasks.register("downloadClientJar") {
    group = "mojang"
    description = "Downloads the Minecraft $minecraftVersion client jar to mojang/client (if it isn't there yet)."
    val jar = clientJar.asFile
    outputs.file(jar)
    onlyIf { !jar.isFile }
    doLast { downloadMinecraft("client", jar) }
}

tasks.register<Sync>("downloadClient") {
    group = "mojang"
    description = "Downloads the Minecraft $minecraftVersion client jar to mojang/client and extracts its assets there."
    dependsOn(downloadClientJar)
    from(zipTree(clientJar)) { include("assets/**") }
    into(clientDir)
    preserve { include("*.jar") } // keep the jars; drop assets left over from another version
}

val downloadServer = tasks.register("downloadServer") {
    group = "mojang"
    description = "Downloads the Minecraft $minecraftVersion server jar to mojang/server (if it isn't there yet)."
    val jar = serverJar.asFile
    outputs.file(jar)
    onlyIf { !jar.isFile }
    doLast { downloadMinecraft("server", jar) }
}

tasks.register<JavaExec>("runServer") {
    group = "mojang"
    description = "Runs the Minecraft $minecraftVersion server in mojang/server, offline mode, port 25565, flying allowed."
    dependsOn(downloadServer)
    workingDir = serverDir.asFile
    classpath(serverJar)
    mainClass.set("net.minecraft.bundler.Main")
    args("nogui")
    maxHeapSize = "2G"
    standardInput = System.`in` // type server commands (e.g. stop) into the Gradle console

    doFirst {
        val dir = serverDir.asFile
        // Accepting the Minecraft EULA is up to you, so this never does it for you.
        val eula = File(dir, "eula.txt")
        if (!eula.isFile || !eula.readText().contains("eula=true")) {
            if (!eula.isFile) eula.writeText("# https://aka.ms/MinecraftEULA\neula=false\n")
            throw GradleException("Read the Minecraft EULA at https://aka.ms/MinecraftEULA and, if you agree, " +
                    "set eula=true in ${eula.relativeTo(projectDir)}, then run this task again.")
        }

        // offline mode (NanoCraft doesn't log in to Microsoft accounts) on the default port, no whitelist,
        // new players in spectator mode, and flying allowed: NanoCraft's camera flies, and the server
        // kicks players who float for too long in other game modes
        val properties = File(dir, "server.properties")
        val wanted = mapOf("online-mode" to "false", "server-port" to "25565", "white-list" to "false",
            "gamemode" to "spectator", "allow-flight" to "true", "force-gamemode" to "true")
        val lines = if (properties.isFile) properties.readLines().toMutableList() else mutableListOf()
        for ((key, value) in wanted) {
            val i = lines.indexOfFirst { it.startsWith("$key=") }
            if (i >= 0) lines[i] = "$key=$value" else lines.add("$key=$value")
        }
        properties.writeText(lines.joinToString("\n") + "\n")
    }
}

// ---------------------------------------------------------------------------
// Tools (tools/*.py) run on the downloaded client jar. Need python3 with Pillow.
// ---------------------------------------------------------------------------

fun registerTool(name: String, script: String, text: String) = tasks.register<Exec>(name) {
    group = "tools"
    description = text
    dependsOn(downloadClientJar)
    workingDir = projectDir
    commandLine("python3", "tools/$script", clientJar.asFile.absolutePath)
}

val chestTextures = registerTool("chestTextures", "chest_textures.py",
    "Writes block-style chest textures to src/resources/assets/texture/block.")
val tintedTextures = registerTool("tintedTextures", "tinted_textures.py",
    "Writes pre-colored grass/foliage/water/redstone/stem textures to src/resources/assets/texture/block.")
registerTool("importVanillaModels", "import_vanilla_models.py",
    "Regenerates model/block/vanilla.shp and def/block/vanilla.def from the vanilla block models.")
registerTool("biomeColors", "biome_colors.py",
    "Regenerates data/biome/grass_colors.txt from the vanilla biomes.")

tasks.register("generateTextures") {
    group = "tools"
    description = "Runs chestTextures and tintedTextures (overwrites same-named textures in src/resources/assets/texture/block)."
    dependsOn(chestTextures, tintedTextures)
}

val copyBlockTextures = tasks.register<Copy>("copyBlockTextures") {
    group = "tools"
    description = "Copies all vanilla block textures from mojang/client into src/resources/assets/texture/block " +
            "(overwrites same-named files; run generateTextures afterwards for the colored/chest ones)."
    dependsOn("downloadClient")
    from(clientDir.dir("assets/minecraft/textures/block")) { include("*.png") }
    into(layout.projectDirectory.dir("src/resources/assets/texture/block"))
}
// when run together, the generated textures win over the plain vanilla copies
chestTextures.configure { mustRunAfter(copyBlockTextures) }
tintedTextures.configure { mustRunAfter(copyBlockTextures) }

val generateReports = tasks.register<JavaExec>("generateReports") {
    group = "mojang"
    description = "Runs the vanilla data generator on the server jar, writing its reports (blocks, packets, ...) to build/mojang/datagen/reports."
    dependsOn(downloadServer)
    val dir = layout.buildDirectory.dir("mojang").get().asFile
    workingDir = dir // the server jar unpacks its libraries here
    classpath(serverJar)
    mainClass.set("net.minecraft.bundler.Main")
    systemProperty("bundlerMainClass", "net.minecraft.data.Main")
    args("--reports", "--output", File(dir, "datagen").absolutePath)
    outputs.dir(File(dir, "datagen"))
    doFirst { dir.mkdirs() }
}

tasks.register<Exec>("generateBlockstates") {
    group = "tools"
    description = "Regenerates data/block/blockstates.txt (block state ids) from the vanilla data generator's blocks report."
    dependsOn(generateReports)
    workingDir = projectDir
    commandLine("python3", "tools/blockstates.py", layout.buildDirectory.file("mojang/datagen/reports/blocks.json").get().asFile.absolutePath)
}
