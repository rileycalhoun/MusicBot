
plugins {
    kotlin("jvm") version "1.7.10"
    id("com.github.johnrengelman.shadow") version "5.2.0"
    id("org.sonarqube") version "3.5.0.2730"
}

group = "dev.blackcandletech"
version = "1.0"

repositories {
    mavenCentral()
    maven("https://m2.dv8tion.net/releases")
}

tasks.withType<Jar> {
    manifest {
        attributes["Main-Class"] = "dev.blackcandletech.parkway.Parkway"
    }
}

dependencies {
    implementation ("net.dv8tion:JDA:5.0.0-beta.2")
    implementation ("com.sedmelluq:lavaplayer:1.3.78")
    implementation ("org.reflections:reflections:0.10.2")

    implementation ("org.slf4j:slf4j-simple:2.0.6")
    implementation ("org.slf4j:slf4j-api:2.0.6")

    implementation("org.litote.kmongo:kmongo:4.8.0")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.4.1")
}

sonarqube {
    properties {
        property("sonar.projectKey", "black-candle-technologies_JDABot")
        property("sonar.organization", "black-candle-technologies")
        property("sonar.host.url", "https://sonarcloud.io")
    }
}