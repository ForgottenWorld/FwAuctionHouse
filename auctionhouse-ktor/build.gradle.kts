
plugins {
    application
    kotlin("jvm")
    id("org.jetbrains.kotlin.plugin.serialization") version "1.5.31"
}

application {
    mainClass.set("me.kaotich00.fwauctionhouse.ktor.ApplicationKt")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":auctionhouse-db"))

    implementation(Libs.Ktor.serverCore)
    implementation(Libs.Ktor.webSockets)
    implementation(Libs.Ktor.serialization)
    implementation(Libs.Ktor.netty)
    implementation(Libs.Ktor.logBack)

    implementation(Libs.Exposed.core)
    implementation(Libs.Exposed.dao)
    implementation(Libs.Exposed.jdbc)
    implementation(Libs.Exposed.javaTime)

    implementation(Libs.hikariCP)
    implementation(Libs.mySqlConnector)
}