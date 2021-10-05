package me.kaotich00.fwauctionhouse.ktor.listing

import io.ktor.application.*
import io.ktor.response.*
import io.ktor.routing.*
import me.kaotich00.fwauctionhouse.db.listing.Listing
import me.kaotich00.fwauctionhouse.db.listing.Listings
import me.kaotich00.fwauctionhouse.ktor.validateCall
import org.jetbrains.exposed.sql.transactions.transaction

fun Route.listingListRoute() {
    get("/list") {
        if (!validateCall(call)) return@get

        call.respond(
            transaction {
                Listing.find {
                    Listings.minecraftEnum neq ""
                }.groupBy {
                    it.minecraftEnum
                }.map { (k, v) ->
                    mapOf(
                        "minecraftEnum" to k,
                        "minUnitPrice" to (v.minByOrNull { it.unitPrice }?.unitPrice ?: 0f).toString(),
                        "totalCount" to v.size.toString()
                    )
                }
            }
        )
    }
}