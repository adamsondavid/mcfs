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

tasks.register("buildSpigot") {
    group = "server"
    description = "downloads spigot's BuildTools.jar and builds a spigot server executable"
    doLast {
        if (!file("build/spigot/spigot-$spigotVersion.jar").exists()) {
            download.run {
                src("https://hub.spigotmc.org/jenkins/job/BuildTools/lastSuccessfulBuild/artifact/target/BuildTools.jar")
                dest("build/spigot/BuildTools.jar")
            }
            exec {
                workingDir("build/spigot")
                commandLine("java", "-jar", "BuildTools.jar", "--rev", spigotVersion)
            }
        }
    }
}

tasks.register("buildServer") {
    group = "server"
    description = "Builds the server that contains of the spigot server executable, the plugin and config"
    dependsOn("buildSpigot", "shadowJar")
    doLast {
        file("server").copyRecursively(File("build/dist"), true)
        file("build/spigot/spigot-$spigotVersion.jar").copyTo(File("build/dist/spigot.jar"), true)
        //TODO: uncomment once plugin is working file("build/libs/${rootProject.name}-$version-all.jar").copyTo(File("build/dist/plugins/${rootProject.name}-$version.jar"), true)
    }
}

tasks.register("run", JavaExec::class) {
    group = "server"
    description = "Runs the built server"
    dependsOn("buildServer")
    workingDir = file("build/dist")
    classpath = files("build/dist/spigot.jar")
    jvmArgs = listOf("-DIReallyKnowWhatIAmDoingISwear")
    args = listOf("nogui")
}
