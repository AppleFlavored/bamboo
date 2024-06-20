plugins {
    id("java")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":"))
    implementation("net.minestom:minestom-snapshots:f1d5940855")
}