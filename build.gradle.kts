plugins {
    kotlin("jvm") version "2.2.0"
    id("com.github.johnrengelman.shadow") version "8.1.1"
    id("de.undercouch.download") version "5.6.0"
}

kotlin {
    jvmToolchain(21)
}

group = "io.github.adamsondavid"
version = "0.0.0"
val spigotVersion = "1.21.8"

repositories {
    mavenCentral()
}

dependencies {
}

tasks.register("buildServer") {
    group = "build"
    description = "Builds the dist directory containing a runnable spigot server with the plugin"
    dependsOn("shadowJar")
    doLast {
        if (!file("dist/spigot-$spigotVersion.jar").exists()) {
            download.run {
                src("https://hub.spigotmc.org/jenkins/job/BuildTools/lastSuccessfulBuild/artifact/target/BuildTools.jar")
                dest("build/spigot/BuildTools.jar")
            }
            exec {
                workingDir("build/spigot")
                commandLine("java", "-jar", "BuildTools.jar", "--rev", spigotVersion)
            }
            file("build/spigot/spigot-$spigotVersion.jar").copyTo(File("dist/spigot-$spigotVersion.jar"))
        }
        file("build/libs/${rootProject.name}-$version-all.jar").copyTo(File("dist/plugins/${rootProject.name}-$version.jar"), true)
    }
}

tasks.register("dev", JavaExec::class) {
    group = "run"
    description = "Runs the built server"
    dependsOn("buildServer")
    workingDir = file("dist")
    classpath = files("dist/spigot-$spigotVersion.jar")
    jvmArgs = listOf("-DIReallyKnowWhatIAmDoingISwear")
    args = listOf("nogui")
}
