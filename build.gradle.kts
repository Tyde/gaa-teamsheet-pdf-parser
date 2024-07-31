plugins {
    kotlin("jvm") version "1.9.22"
    kotlin("plugin.serialization") version "2.0.0"
    `java-library`
}

group = "eu.gaelicgames"
version = "0.1"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation("org.jetbrains.kotlin:kotlin-test")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.7.1")
    implementation("org.apache.pdfbox:pdfbox:3.0.2")

}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(17)
}
