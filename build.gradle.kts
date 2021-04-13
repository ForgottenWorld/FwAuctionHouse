import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.4.32"
    id("com.github.johnrengelman.shadow") version "6.1.0"
    id("com.github.ben-manes.versions") version "0.38.0"
}

repositories {
    maven("https://papermc.io/repo/repository/maven-public/")
    maven("https://oss.sonatype.org/content/groups/public/")
    maven("https://jitpack.io")
    maven("https://repo.maven.apache.org/maven2/")
}

dependencies {
    implementation("com.zaxxer:HikariCP:4.0.3")
    implementation("org.slf4j:slf4j-simple:1.6.4")
    compileOnly("com.destroystokyo.paper:paper-api:1.16.5-R0.1-SNAPSHOT")
    compileOnly("com.github.MilkBowl:VaultAPI:1.7")
    compileOnly("com.github.BrunoSilvaFreire:Skedule:0.1.3")
    compileOnly("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.4.0")
    compileOnly("com.github.TownyAdvanced:Towny:0.96.7.12")

    val guiceVersion = "5.0.1"

    implementation("com.google.inject:guice:$guiceVersion")
}

tasks.withType<ShadowJar> {
    relocate("com.zaxxer.hikari", "me.kaotich00.fwauctionhouse.zaxxer.hikari")
    relocate("org.slf4j", "me.kaotich00.fwauctionhouse.slf4j")
    relocate("javax.inject", "me.kaotich00.fwauctionhouse.javax.inject")
    relocate("com.google.inject", "me.kaotich00.fwauctionhouse.google.inject")
}

group = "me.kaotich00"
version = "0.0.1"
description = "FwAuctionHouse"

java.sourceCompatibility = JavaVersion.VERSION_1_8

tasks.withType<JavaCompile> {
    sourceCompatibility = "1.8"
    targetCompatibility = "1.8"
    options.encoding = "UTF-8"
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        jvmTarget = "1.8"
    }
}