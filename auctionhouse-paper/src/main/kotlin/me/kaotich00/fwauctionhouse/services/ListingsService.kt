package me.kaotich00.fwauctionhouse.services

import me.kaotich00.fwauctionhouse.db.listing.Listing
import me.kaotich00.fwauctionhouse.db.session.PlayerSession

interface ListingsService {

    fun scheduleSellingTask()

    fun scheduleTokenConfirmationTask()

    fun removePendingPurchase(listing: Listing)

    fun getPendingPurchase(id: Int): Listing?

    fun removePlayerSession(playerSession: PlayerSession)

    fun getPlayerSession(id: Int): PlayerSession?
}