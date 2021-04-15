package me.kaotich00.fwauctionhouse.services

import me.kaotich00.fwauctionhouse.model.listing.Listing
import me.kaotich00.fwauctionhouse.model.session.PlayerSession

interface ListingsService {

    fun scheduleSellingTask()

    fun scheduleConfirmTokenTask()

    fun removeFromListings(listing: Listing)

    fun getListing(id: Int): Listing?

    fun removeFromPlayerSession(playerSession: PlayerSession)

    fun getPlayerSession(id: Int): PlayerSession?
}