plugins {
    kotlin("jvm") version "2.2.0"
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

kotlin {
    jvmToolchain(21)
}

group = "io.github.adamsondavid"
version = "0.0.0"

repositories {
    mavenCentral()
}

dependencies {
}

