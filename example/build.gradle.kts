plugins {
    id("java")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":"))
    implementation("net.minestom:minestom-snapshots:698af959c8")
}