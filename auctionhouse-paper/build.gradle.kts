import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    `maven-publish`
    kotlin("jvm")
    id("com.github.johnrengelman.shadow") version "6.1.0"
}

dependencies {
    implementation(project(":auctionhouse-db"))

    implementation(Libs.hikariCP)
    implementation(Libs.slf4j)

    compileOnly(Libs.paperApi)
    compileOnly(Libs.vaultApi)
    compileOnly(Libs.skedule)
    compileOnly(Libs.coroutinesCore)
    compileOnly(Libs.towny)
    compileOnly(Libs.koHttp)

    compileOnly(Libs.Exposed.core)
    compileOnly(Libs.Exposed.dao)
    compileOnly(Libs.Exposed.jdbc)
    compileOnly(Libs.Exposed.javaTime)

    implementation(Libs.Guice.guice)
}

tasks.withType<ShadowJar> {
    dependencies {
        val keep = listOf(
            "me.kaotich00.fwauctionhouse",
            "com.google.inject",
            "javax.inject",
            "org.slf4j",
            "aopalliance",
            "com.zaxxer"
        )

        exclude {
            keep.none(it.moduleGroup::startsWith)
        }
    }

    relocate("org.aopalliance", "me.kaotich00.fwauctionhouse.aopalliance")
    relocate("com.zaxxer", "me.kaotich00.fwauctionhouse.zaxxer")
    relocate("org.slf4j", "me.kaotich00.fwauctionhouse.slf4j")
    relocate("javax.inject", "me.kaotich00.fwauctionhouse.javax.inject")
    relocate("com.google.inject", "me.kaotich00.fwauctionhouse.google.inject")
}

java.sourceCompatibility = JavaVersion.VERSION_1_8

tasks.register("localDeploy") {
    doLast {
        copy {
            from("build/libs")
            into("/home/giacomo/paper/plugins")
            include("**/*-all.jar")
        }
    }
}