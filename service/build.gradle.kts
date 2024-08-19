plugins {
    id("de.espirit.firstspirit-module-annotations")
}

dependencies {
    compileOnly(libs.firstspirit.runtime)
    implementation(libs.slf4j.api)
    implementation(group = "tablelayout", name = "TableLayout", version = "20050920")

    implementation(project(":service-api"))
}