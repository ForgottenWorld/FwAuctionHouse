package me.kaotich00.fwauctionhouse.storage

import me.kaotich00.fwauctionhouse.model.listing.Listing
import me.kaotich00.fwauctionhouse.model.listing.ListingStatus
import me.kaotich00.fwauctionhouse.model.session.PlayerSession
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

interface ListingsDao {

    fun insertListing(seller: Player, itemStack: ItemStack, unitPrice: Double)

    fun getListings(): List<Listing>

    fun updateListingStatus(listingId: Int, status: ListingStatus)

    fun deleteListing(listingId: Int)

    fun getPlayerSessions(): List<PlayerSession>

    fun validateToken(sessionId: Int)
}