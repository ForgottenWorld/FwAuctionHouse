package me.kaotich00.fwauctionhouse.ktor

import io.ktor.application.*
import io.ktor.features.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import me.kaotich00.fwauctionhouse.db.connection.hikari.MySQLDatabaseConnectionManager
import me.kaotich00.fwauctionhouse.db.connection.util.DatabaseCredentials
import me.kaotich00.fwauctionhouse.ktor.plugins.*
import java.io.File

fun main() {
    val json = Json { prettyPrint = true }
    val configFile = File("config.json")

    val config = if (!configFile.exists()) {
        Configuration.DEFAULT.also {
            configFile.writeText(json.encodeToString(it))
        }
    } else {
        json.decodeFromString(configFile.readText())
    }

    val credentials = DatabaseCredentials(
        host = "${config.dbAddress}:${config.dbPort}",
        database = config.dbName,
        username = config.dbUsername,
        password = config.dbPassword,
    )

    MySQLDatabaseConnectionManager().init(credentials)

    ApiGuard.initialize(config)

    embeddedServer(Netty, port = config.apiPort, host = config.apiAddress) {
        install(CORS) {
            anyHost()
        }

        configureRouting()
        configureSockets()
        configureSerialization()
    }.start(wait = true)
}
