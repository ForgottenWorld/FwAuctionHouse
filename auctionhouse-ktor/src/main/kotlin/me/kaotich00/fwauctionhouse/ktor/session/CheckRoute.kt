package me.kaotich00.fwauctionhouse.ktor.session

import io.ktor.application.*
import io.ktor.response.*
import io.ktor.routing.*
import me.kaotich00.fwauctionhouse.ktor.validateToken

fun Route.sessionCheckRoute() {
    get("/check/{token}") {
        val token = call.parameters["token"] ?: return@get

        call.respond(validateToken(token))
    }
}