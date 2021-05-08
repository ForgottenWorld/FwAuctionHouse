package me.kaotich00.fwauctionhouse.ktor.events

import io.ktor.application.*
import io.ktor.routing.*
import me.kaotich00.fwauctionhouse.ktor.ApiGuard
import me.kaotich00.fwauctionhouse.ktor.WebSocketEventManager

fun Route.onListingsChangedRoute() {
    get("/onListingsChanged") {
        val apiKey = call.request.queryParameters["api_key"] ?: return@get

        if (!ApiGuard.validateRequest(apiKey)) return@get

        WebSocketEventManager.onListingsChanged()
    }
}