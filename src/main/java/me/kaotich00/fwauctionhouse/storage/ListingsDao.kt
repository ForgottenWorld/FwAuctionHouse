package me.kaotich00.fwauctionhouse.storage

import me.kaotich00.fwauctionhouse.model.PendingSell
import me.kaotich00.fwauctionhouse.model.PendingToken
import me.kaotich00.fwauctionhouse.model.ListingStatus
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

interface ListingsDao {

    fun insertListing(seller: Player, itemStack: ItemStack, unitPrice: Double)

    fun getPendingSells(): List<PendingSell>

    fun updateListingStatus(listingId: Int, status: ListingStatus)

    fun deletePendingSell(listingId: Int)

    fun getPendingTokens(): List<PendingToken>

    fun validateToken(sessionId: Int)
}