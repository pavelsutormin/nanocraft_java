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
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    from(configurations.runtimeClasspath.get().map { if (it.isDirectory) it else zipTree(it) }) {
        exclude("META-INF/*.SF")
        exclude("META-INF/*.DSA")
        exclude("META-INF/*.RSA")
    }
}

tasks.register<JavaExec>("runJar") {
    group = "custom"
    description = "Builds and runs the Nanocraft jar file."

    val jarTask = tasks.named<org.gradle.jvm.tasks.Jar>("jar").get()
    dependsOn(jarTask)

    classpath(jarTask.archiveFile)
}
