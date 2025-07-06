plugins {
    id("java")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":"))
    implementation("net.minestom:minestom:2025.07.04-1.21.5")
}