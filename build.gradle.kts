plugins {
    kotlin("jvm") version "1.9.20"
    `maven-publish`
}

group = "dev.flavored"
version = "0.1.0"

repositories {
    mavenCentral()
    maven(url = "https://jitpack.io")
}

dependencies {
    compileOnly("com.github.Minestom:Minestom:c496ee357")
    testImplementation("dev.hollowcube:minestom-ce:438338381e")
}

kotlin {
    jvmToolchain(17)
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["java"])
        }
    }
}