import org.gradle.kotlin.dsl.accessors.runtime.extensionOf
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.8.0"
    application
}

group = "me.user"
version = "1.0-SNAPSHOT"


repositories {
    mavenCentral()
    google()
}

dependencies {
    implementation("io.ktor:ktor-server-core:2.2.2")
    implementation("io.ktor:ktor-server-netty:2.2.2")

    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "14"
}

application {
    mainClass.set("playground.ktor.Hello")
}