plugins {
    kotlin("jvm") version "2.3.0"
    id("application")
    id("com.gradleup.shadow") version "8.3.6"
}

group = "net.bbo51dog"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
    implementation("ch.qos.logback:logback-classic:1.5.13")
    implementation("net.dv8tion:JDA:6.2.1")
    implementation("org.json:json:20251224")
}

kotlin {
    jvmToolchain(25)
}

tasks.test {
    useJUnitPlatform()
}

application {
    mainClass = "net.bbo51dog.arkdiscord.MainKt"
}

val jar by tasks.getting(Jar::class) {
    manifest {
        attributes["Main-Class"] = "net.bbo51dog.arkdiscord.MainKt"
    }
}