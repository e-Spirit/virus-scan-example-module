plugins {
    `java-library`
    id("de.espirit.firstspirit-module") version "6.4.1" apply false
}

allprojects {
    apply(plugin = "java-library")

    java {
        toolchain {
            languageVersion = JavaLanguageVersion.of(17)
        }
    }

    repositories {
        repositories {
            maven(url = "https://artifactory.e-spirit.hosting/artifactory/repo/") {
                credentials {
                    username = property("artifactory_hosting_username") as String
                    password = property("artifactory_hosting_password") as String
                }
            }
        }
    }
}

val publish by tasks.registering // Required by Bamboo plan