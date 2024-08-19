pluginManagement {
    val artifactory_hosting_username: String by settings
    val artifactory_hosting_password: String by settings

    repositories {
        maven(url = "https://artifactory.e-spirit.hosting/artifactory/repo") {
            credentials {
                username = artifactory_hosting_username
                password = artifactory_hosting_password
            }
        }
    }
}

plugins {
    // Apply the foojay-resolver plugin to allow automatic download of JDKs
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0"
}

rootProject.name = "virus-scan-example-module"
include("clamav-engine")
include("module")
include("service")
include("service-api")
