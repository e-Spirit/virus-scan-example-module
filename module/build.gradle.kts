plugins {
    id("de.espirit.firstspirit-module")
}

dependencies {
    compileOnly(libs.firstspirit.runtime)

    fsModuleCompile(libs.slf4j.api)
    fsModuleCompile(project(":clamav-engine"))
    fsModuleCompile(project(":service"))

    fsServerCompile(project(":service-api"))

    testImplementation(platform("org.junit:junit-bom:5.11.0"))
    testImplementation(group = "org.junit.jupiter", name = "junit-jupiter")
    testImplementation(group = "com.tngtech.archunit", name = "archunit-junit5", version = "1.3.0")
    testImplementation(libs.firstspirit.runtime)
}

firstSpiritModule {
    moduleName = "FS VScan Service"
    displayName = "FS VScan Service (I)"
    description = "Pluggable Virus Scanning Service"
    vendor = "Crownpeak Technology GmbH"
}

tasks.assembleFSM {
    archiveFileName = "fs-vscan.fsm"
}

tasks.jar {
    archiveBaseName = "fs-vscan"
}

tasks.withType<Test> {
    useJUnitPlatform()
}
