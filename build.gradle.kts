import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    `maven-publish`
    id("com.github.johnrengelman.shadow") version "7.1.2"
    kotlin("jvm") version "1.9.0"
    kotlin("plugin.serialization") version "1.9.0"
}

group = "io.liftgate.aware.kotlinx"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    compileOnly(kotlin("stdlib"))
    testImplementation(kotlin("test"))

    compileOnly("com.github.ben-manes.caffeine:caffeine:3.1.0")
    compileOnly("io.lettuce:lettuce-core:6.2.3.RELEASE")
    compileOnly("org.jetbrains.kotlinx:kotlinx-serialization-json-jvm:1.5.1")

    compileOnly("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
    compileOnly("org.jetbrains.kotlinx:kotlinx-coroutines-jdk8:1.7.3")

    testImplementation("com.github.ben-manes.caffeine:caffeine:3.1.0")
    testImplementation("io.lettuce:lettuce-core:6.2.3.RELEASE")
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
    kotlinOptions.javaParameters = true
    kotlinOptions.jvmTarget = "17"
}

tasks.withType<ShadowJar> {
    archiveClassifier.set("")
    archiveFileName.set(
        "aware-kotlinx.jar"
    )
}

publishing {
    publications {
        register(
            name = "mavenJava",
            type = MavenPublication::class,
            configurationAction = shadow::component
        )
    }
}
