plugins {
    kotlin("jvm") version "1.9.22"
    kotlin("plugin.serialization") version "2.0.0"
    `java-library`
    `maven-publish`
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


publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = project.group.toString()
            artifactId = project.name
            version = project.version.toString()

            from(components["java"])
        }
    }
}

tasks.named("publishToMavenLocal") {
    dependsOn("assemble")
}
