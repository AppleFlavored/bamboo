plugins {
    kotlin("jvm") version "1.9.20"
    `java-library`
    `maven-publish`
    signing
}

group = "dev.flavored"
version = "0.1.0"

repositories {
    mavenCentral()
}

dependencies {
    compileOnly("net.minestom:minestom-snapshots:f1d5940855")
}

java {
    withJavadocJar()
    withSourcesJar()
}

kotlin {
    jvmToolchain(21)
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])

            pom {
                name = "bamboo"
                url = "https://github.com/AppleFlavored/bamboo"
                description = "A fast and lightweight schematic library for Minestom."

                licenses {
                    license {
                        name = "MIT License"
                        url = "https://opensource.org/license/mit/"
                    }
                }

                developers {
                    developer {
                        id = "AppleFlavored"
                        name = "AppleFlavored"
                        url = "https://github.com/AppleFlavored"
                    }
                }

                scm {
                    connection = "scm:git:git://github.com/AppleFlavored/bamboo.git"
                    developerConnection = "scm:git:ssh://github.com:AppleFlavored/bamboo.git"
                    url = "https://github.com/AppleFlavored/bamboo"
                }
            }
        }
    }

    repositories {
        maven {
            name = "OSSRH"
            url = uri("https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/")
            credentials {
                username = System.getenv("MAVEN_USERNAME")
                password = System.getenv("MAVEN_PASSWORD")
            }
        }
    }
}

signing {
    sign(publishing.publications["mavenJava"])
}