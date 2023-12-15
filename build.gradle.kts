plugins {
    kotlin("jvm") version "1.9.20"
    `maven-publish`
    signing
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

java {
    withJavadocJar()
    withSourcesJar()
}

kotlin {
    jvmToolchain(17)
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