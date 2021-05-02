package me.kaotich00.fwauctionhouse.ktor.listing

import io.ktor.application.*
import io.ktor.response.*
import io.ktor.routing.*
import me.kaotich00.fwauctionhouse.db.listing.Listing
import me.kaotich00.fwauctionhouse.db.listing.Listings
import me.kaotich00.fwauctionhouse.ktor.toMap
import me.kaotich00.fwauctionhouse.ktor.validateCall
import me.kaotich00.fwauctionhouse.ktor.validateToken
import org.jetbrains.exposed.sql.transactions.transaction

fun Route.listingListByEnumRoute() {
    get("/listByEnum/{enum}") {
        val enum = call.parameters["enum"] ?: return@get
        if (!validateCall(call)) return@get

        call.respond(
            transaction {
                Listing.find { Listings.minecraftEnum eq enum }.map { it.toMap() }
            }
        )
    }
}