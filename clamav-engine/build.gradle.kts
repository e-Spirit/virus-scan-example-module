plugins {
    id("de.espirit.firstspirit-module-annotations")
}

dependencies {
    compileOnly(libs.firstspirit.runtime)
    implementation(libs.slf4j.api)

    implementation(project(":service"))
    implementation(project(":service-api"))
}