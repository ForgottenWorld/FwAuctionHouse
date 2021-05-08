package me.kaotich00.fwauctionhouse.services

import com.google.inject.Inject
import com.google.inject.Singleton
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import me.kaotich00.fwauctionhouse.FwAuctionHouse
import me.kaotich00.fwauctionhouse.message.Message
import me.kaotich00.fwauctionhouse.message.TextComponents
import me.kaotich00.fwauctionhouse.db.listing.Listing
import me.kaotich00.fwauctionhouse.storage.ListingsDao
import me.kaotich00.fwauctionhouse.utils.BukkitDispatchers
import me.kaotich00.fwauctionhouse.db.session.PlayerSession
import me.kaotich00.fwauctionhouse.message.send
import me.kaotich00.fwauctionhouse.model.itemStack
import me.kaotich00.fwauctionhouse.model.total
import me.kaotich00.fwauctionhouse.utils.launch
import org.bukkit.Bukkit

@Singleton
class ListingsServiceImpl @Inject constructor(
    private val listingsDao: ListingsDao
) : ListingsService {

    private val pendingPurchases = mutableMapOf<Int, Listing>()

    private val playerSessions = mutableMapOf<Int, PlayerSession>()


    override fun scheduleSellingTask() {
        launch {
            while(true) {
                delay(1000)

                val listings = withContext(BukkitDispatchers.async) {
                    listingsDao.getListings()
                }

                for (listing in listings) processPendingSell(listing)
            }
        }
    }

    private fun processPendingSell(listing: Listing) {
        val buyerName = listing.buyerName ?: return

        val player = Bukkit.getPlayer(buyerName)

        if (player == null) {
            if (Bukkit.getOfflinePlayerIfCached(buyerName) == null) {
                listingsDao.updateListingStatus(listing.id.value, Listing.Status.NO_USER_FOUND)
            }
            return
        }

        if (getPendingPurchase(listing.id.value) != null) return

        if (player.inventory.firstEmpty() == -1) {
            Message.INVENTORY_FULL.send(player)
            return
        }

        if (FwAuctionHouse.economy.getBalance(player) < listing.total) {
            Message.NOT_ENOUGH_MONEY.send(player)
            listingsDao.updateListingStatus(listing.id.value, Listing.Status.NOT_ENOUGH_MONEY)
            return
        }

        this@ListingsServiceImpl.pendingPurchases[listing.id.value] = listing

        TextComponents.purchaseConfirmation(listing.id.value, listing.itemStack).send(player)
    }

    override fun scheduleTokenConfirmationTask() {
        launch {
            while(true) {
                delay(2000)

                val playerSessions = withContext(BukkitDispatchers.async) {
                    listingsDao.getPlayerSessions()
                }

                for (playerSession in playerSessions) processTokenConfirmation(playerSession)
            }
        }
    }

    private fun processTokenConfirmation(playerSession: PlayerSession) {
        val player = Bukkit.getPlayer(playerSession.username) ?: return

        if (getPlayerSession(playerSession.id.value) != null) return

        this@ListingsServiceImpl.playerSessions[playerSession.id.value] = playerSession

        TextComponents.tokenConfirmation(playerSession.id.value).send(player)
    }

    override fun removePendingPurchase(listing: Listing) {
        pendingPurchases.remove(listing.id.value)
    }

    override fun getPendingPurchase(id: Int) = pendingPurchases[id]

    override fun removePlayerSession(playerSession: PlayerSession) {
        playerSessions.remove(playerSession.id.value)
    }

    override fun getPlayerSession(id: Int) = playerSessions[id]
}