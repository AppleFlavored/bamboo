plugins {
    `java-library`
    `maven-publish`
    signing
    id("io.github.gradle-nexus.publish-plugin") version "2.0.0"
}

group = "dev.flavored"
version = "1.1.2"

repositories {
    mavenCentral()
}

dependencies {
    compileOnly("net.minestom:minestom:2025.07.04-1.21.5")
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

nexusPublishing {
    useStaging = true
    packageGroup = "dev.flavored"

    repositories.sonatype {
        nexusUrl.set(uri("https://s01.oss.sonatype.org/service/local/"))
        snapshotRepositoryUrl.set(uri("https://s01.oss.sonatype.org/content/repositories/snapshots/"))

        if (System.getenv("SONATYPE_USERNAME") != null) {
            username.set(System.getenv("SONATYPE_USERNAME"))
            password.set(System.getenv("SONATYPE_PASSWORD"))
        }
    }
}

signing {
    val privateKey = System.getenv("GPG_PRIVATE_KEY")
    val passphrase = System.getenv("GPG_PASSWORD")
    useInMemoryPgpKeys(privateKey, passphrase)

    sign(publishing.publications["mavenJava"])
}
