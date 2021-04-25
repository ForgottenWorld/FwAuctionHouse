package me.kaotich00

import io.ktor.server.engine.*
import io.ktor.server.netty.*
import me.kaotich00.fwauctionhouse.ktor.ApiGuard
import me.kaotich00.fwauctionhouse.ktor.plugins.*

fun main() {
    ApiGuard.initialize()
    embeddedServer(Netty, port = 8080, host = "127.0.0.1") {
        configureRouting()
        configureSockets()
        configureSerialization()
    }.start(wait = true)
}
