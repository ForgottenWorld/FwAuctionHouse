package me.kaotich00.fwauctionhouse.storage

import me.kaotich00.fwauctionhouse.FwAuctionHouse
import me.kaotich00.fwauctionhouse.objects.PendingSell
import me.kaotich00.fwauctionhouse.objects.PendingToken
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import java.sql.Connection
import java.sql.SQLException

interface StorageMethod {
    val plugin: FwAuctionHouse
    fun init()
    fun shutdown()

    @get:Throws(SQLException::class)
    val connection: Connection?
    fun insertListing(seller: Player, itemStack: ItemStack, unitPrice: Float?)
    val pendingSells: List<PendingSell>
    fun updateListingStatus(listingId: Int, status: Int)
    fun deletePendingSell(listingId: Int)
    val pendingTokens: List<PendingToken>
    fun validateToken(sessionId: Int)
}