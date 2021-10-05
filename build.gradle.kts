import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

repositories {
    mavenCentral()
}

plugins {
    kotlin("jvm") version Versions.kotlinVersion
    id("com.github.ben-manes.versions") version "0.38.0"
}


subprojects {
    group = "me.kaotich00.fwauctionhouse"
    version = Versions.auctionHouse

    repositories {
        mavenCentral()
        maven("https://papermc.io/repo/repository/maven-public/")
        maven("https://mvn.lumine.io/repository/maven-public/")
        maven("https://oss.sonatype.org/content/groups/public/")
        maven("https://jitpack.io")
        mavenLocal()
    }

    tasks.withType<JavaCompile> {
        sourceCompatibility = "16"
        targetCompatibility = "16"
        options.encoding = "UTF-8"
    }

    tasks.withType<KotlinCompile> {
        kotlinOptions {
            jvmTarget = "16"
        }
    }
}