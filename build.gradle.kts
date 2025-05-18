plugins {
    id("java")
    // id("org.jetbrains.kotlin.jvm") version "2.0.10"
    kotlin("jvm") version "2.0.10"
}

group = "kt.hairinne.SCVM"
version = "Alpha-0.01.0"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(21)
}

tasks.withType<Jar> {
    manifest {
        attributes(
            "Main-Class" to "kt.hairinne.SCVM.MainKt"
        )
    }
    from({
        configurations.runtimeClasspath.get().map { if (it.isDirectory) it else zipTree(it) }
    })
}
