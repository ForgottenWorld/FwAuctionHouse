package me.kaotich00.fwauctionhouse.ktor.plugins

import io.ktor.application.*
import io.ktor.http.cio.websocket.*
import io.ktor.routing.*
import io.ktor.websocket.*
import me.kaotich00.fwauctionhouse.ktor.Connection
import me.kaotich00.fwauctionhouse.ktor.WebSocketEventManager
import java.time.Duration


fun Application.configureSockets() {
    install(WebSockets) {
        pingPeriod = Duration.ofSeconds(15)
        timeout = Duration.ofSeconds(15)
        maxFrameSize = Long.MAX_VALUE
        masking = false
    }

    routing {
        webSocket("/updates") {
            for (frame in incoming) {
                if (frame !is Frame.Text) continue
                val text = frame.readText()
                when {
                    text.startsWith("HELLO") -> {
                        val token = text.split('%').getOrNull(1)?.trim() ?: continue
                        if (token.length != 32) continue
                        val thisConnection = Connection(token, this)
                        WebSocketEventManager.registerConnection(token, thisConnection)
                    }
                    text == "PING" -> {
                        send("PONG")
                    }
                }
            }
        }
    }
}
