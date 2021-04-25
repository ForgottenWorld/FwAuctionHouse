package me.kaotich00.fwauctionhouse.ktor.listing

import io.ktor.application.*
import io.ktor.response.*
import io.ktor.routing.*
import me.kaotich00.fwauctionhouse.db.listing.Listing
import me.kaotich00.fwauctionhouse.ktor.toMap
import me.kaotich00.fwauctionhouse.ktor.validateToken
import org.jetbrains.exposed.sql.transactions.transaction

fun Route.listingGetRoute() {
    get("/get/{id}") {
        val id = call.parameters["id"]?.toIntOrNull() ?: return@get
        val token = call.request.queryParameters["token"] ?: return@get

        val validationResult = validateToken(token)
        if (validationResult["status"] != 1) {
            call.respond(validationResult)
            return@get
        }

        val listing = transaction {
            Listing.findById(id)
        } ?: return@get

        call.respond(listing.toMap())
    }
}