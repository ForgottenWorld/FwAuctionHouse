package me.kaotich00.fwauctionhouse.ktor.listing

import io.ktor.application.*
import io.ktor.response.*
import io.ktor.routing.*
import me.kaotich00.fwauctionhouse.db.listing.Listing
import me.kaotich00.fwauctionhouse.db.session.PlayerSession
import me.kaotich00.fwauctionhouse.db.session.PlayerSessions
import me.kaotich00.fwauctionhouse.ktor.validateToken
import org.jetbrains.exposed.sql.transactions.transaction

fun Route.listingPlaceOrderRoute() {
    get("/placeOrder/{orderId}/{token}") {
        val orderId = call.parameters["orderId"]?.toIntOrNull() ?: return@get
        val token = call.parameters["token"] ?: return@get

        val validationResult = validateToken(token)
        if (validationResult["status"] != "1") {
            call.respond(validationResult)
            return@get
        }

        val session = transaction {
            PlayerSession
                .find { PlayerSessions.token eq token }
                .singleOrNull()
        } ?: return@get

        val listing = transaction {
            Listing.findById(orderId)
        } ?: run {
            call.respond(
                mapOf(
                    "status" to "-4",
                    "error" to "Order for ID $orderId not found."
                )
            )
            return@get
        }

        if (listing.status == Listing.Status.ORDER_PLACED.value) {
            call.respond(
                mapOf(
                    "status" to "-5",
                    "error" to "Order for ID $orderId already placed."
                )
            )
            return@get
        }

        transaction {
            listing.buyerName = session.username
            listing.status = Listing.Status.ORDER_PLACED.value
        }

        call.respond(mapOf("status" to 1))
    }
}