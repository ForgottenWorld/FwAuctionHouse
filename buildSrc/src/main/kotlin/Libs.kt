object Libs {

    const val hikariCP = "com.zaxxer:HikariCP:4.0.3"
    const val slf4j = "org.slf4j:slf4j-simple:1.6.4"

    const val paperApi = "com.destroystokyo.paper:paper-api:1.16.5-R0.1-SNAPSHOT"
    const val vaultApi = "com.github.MilkBowl:VaultAPI:1.7"
    const val skedule = "com.github.BrunoSilvaFreire:Skedule:0.1.3"
    const val coroutinesCore = "org.jetbrains.kotlinx:kotlinx-coroutines-core:1.4.0"
    const val towny = "com.github.TownyAdvanced:Towny:0.96.7.12"

    const val koHttp = "io.github.rybalkinsd:kohttp:0.12.0"

    object Exposed {
        private const val version = "0.30.2"

        const val core = "org.jetbrains.exposed:exposed-core:$version"
        const val dao = "org.jetbrains.exposed:exposed-dao:$version"
        const val jdbc = "org.jetbrains.exposed:exposed-jdbc:$version"
        const val javaTime = "org.jetbrains.exposed:exposed-java-time:$version"
    }

    object  Guice {
        private const val version = "5.0.1"

        const val guice = "com.google.inject:guice:$version"
    }

    object Ktor {
        private const val ktorVersion = "1.5.3"
        private const val logBackVersion = "1.2.3"

        const val serverCore = "io.ktor:ktor-server-core:$ktorVersion"
        const val webSockets = "io.ktor:ktor-websockets:$ktorVersion"
        const val serialization = "io.ktor:ktor-serialization:$ktorVersion"
        const val netty = "io.ktor:ktor-server-netty:$ktorVersion"
        const val logBack = "ch.qos.logback:logback-classic:$logBackVersion"

        const val tests = "io.ktor:ktor-server-tests:$ktorVersion"
    }
}