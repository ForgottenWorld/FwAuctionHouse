package me.kaotich00.fwauctionhouse.ktor.plugins

import io.ktor.application.*
import io.ktor.routing.*
import me.kaotich00.fwauctionhouse.ktor.events.onListingsChangedRoute
import me.kaotich00.fwauctionhouse.ktor.events.onTokenConfirmedRoute
import me.kaotich00.fwauctionhouse.ktor.listing.listingGetRoute
import me.kaotich00.fwauctionhouse.ktor.listing.listingListByEnumRoute
import me.kaotich00.fwauctionhouse.ktor.listing.listingListRoute
import me.kaotich00.fwauctionhouse.ktor.listing.listingPlaceOrderRoute
import me.kaotich00.fwauctionhouse.ktor.session.sessionCheckRoute
import me.kaotich00.fwauctionhouse.ktor.session.sessionUpdateTokenRoute

fun Application.configureRouting() {

    routing {
        route("/api") {
            route("/listing") {
                listingListRoute()
                listingListByEnumRoute()
                listingGetRoute()
                listingPlaceOrderRoute()
            }
            route("/session") {
                sessionUpdateTokenRoute()
                sessionCheckRoute()
            }
            route("/events") {
                onListingsChangedRoute()
                onTokenConfirmedRoute()
            }
        }
    }
}
