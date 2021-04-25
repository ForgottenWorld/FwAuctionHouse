package me.kaotich00.fwauctionhouse.ktor.listing

import io.ktor.application.*
import io.ktor.response.*
import io.ktor.routing.*
import me.kaotich00.fwauctionhouse.db.listing.Listing
import me.kaotich00.fwauctionhouse.db.listing.Listings
import me.kaotich00.fwauctionhouse.ktor.toMap
import me.kaotich00.fwauctionhouse.ktor.validateToken
import org.jetbrains.exposed.sql.transactions.transaction

fun Route.listingListByEnumRoute() {
    get("/listByEnum/{enum}") {
        val enum = call.parameters["enum"] ?: return@get
        val token = call.request.queryParameters["token"] ?: return@get

        val validationResult = validateToken(token)
        if (validationResult["status"] != 1) {
            call.respond(validationResult)
            return@get
        }

        val listing = transaction {
            Listing.find { Listings.minecraftEnum eq enum }
        }

        val res = listing.map { it.toMap() }

        call.respond(res)
    }
}