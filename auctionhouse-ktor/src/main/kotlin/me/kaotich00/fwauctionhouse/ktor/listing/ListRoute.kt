package me.kaotich00.fwauctionhouse.ktor.listing

import io.ktor.application.*
import io.ktor.response.*
import io.ktor.routing.*
import me.kaotich00.fwauctionhouse.db.listing.Listing
import me.kaotich00.fwauctionhouse.db.listing.Listings
import me.kaotich00.fwauctionhouse.ktor.validateToken
import org.jetbrains.exposed.sql.transactions.transaction

fun Route.listingListRoute() {
    get("/list") {
        val token = call.request.queryParameters["token"] ?: return@get

        val validationResult = validateToken(token)
        if (validationResult["status"] != 1) {
            call.respond(validationResult)
            return@get
        }

        call.respond(
            transaction {
                Listing.find { Listings.minecraftEnum neq "" }
            }.groupBy {
                it.minecraftEnum
            }.mapValues { (_, v) -> mapOf(
                "minUnitPrice" to (v.minByOrNull { it.unitPrice }?.unitPrice ?: 0f),
                "totalCount" to v.size
            ) }
        )
    }
}