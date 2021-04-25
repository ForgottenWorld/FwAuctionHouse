package me.kaotich00.fwauctionhouse.ktor

import io.ktor.http.cio.websocket.*

class Connection(val token: String, val session: DefaultWebSocketSession)