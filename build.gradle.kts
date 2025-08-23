plugins {
    `java-library`
    `maven-publish`
    signing
    id("com.gradleup.nmcp.aggregation") version("1.1.0")
}

group = "dev.flavored"
version = "1.1.2"

repositories {
    mavenCentral()
}

dependencies {
    compileOnly("net.minestom:minestom:2025.08.18-1.21.8")

    nmcpAggregation(rootProject)
}

java {
    withJavadocJar()
    withSourcesJar()
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
}

nmcpAggregation {
    centralPortal {
        username = System.getenv("SONATYPE_USERNAME")
        password = System.getenv("SONATYPE_PASSWORD")
        publishingType = "AUTOMATIC"
    }
}

signing {
    val privateKey = System.getenv("GPG_PRIVATE_KEY")
    val passphrase = System.getenv("GPG_PASSWORD")
    useInMemoryPgpKeys(privateKey, passphrase)

    sign(publishing.publications["mavenJava"])
}
