package me.kaotich00.fwauctionhouse.commands.user

import com.google.inject.Inject
import me.kaotich00.fwauctionhouse.commands.api.PlayerCommandHandler
import me.kaotich00.fwauctionhouse.db.listing.Listing
import me.kaotich00.fwauctionhouse.message.Message
import me.kaotich00.fwauctionhouse.services.ListingsService
import me.kaotich00.fwauctionhouse.services.WebApiService
import me.kaotich00.fwauctionhouse.storage.ListingsDao
import org.bukkit.entity.Player

class DeclineCommandHandler @Inject constructor(
    private val listingsService: ListingsService,
    private val listingsDao: ListingsDao,
    private val webApiService: WebApiService
) : PlayerCommandHandler(
    name = "decline",
    requiredArgs = 1,
    usage = "/market decline <listingId>",
    info = "Decline a purchase made on the web store"
) {
    override fun onCommand(sender: Player, args: Array<String>) {
        val listingId = args[1].toIntOrNull() ?: run {
            Message.INSERT_VALID_ID.send(sender)
            return
        }

        val listing = listingsService.getPendingPurchase(listingId) ?: return

        Message.DECLINED.send(sender)
        listingsDao.updateListingStatus(listing.id.value, Listing.Status.ORDER_AVAILABLE)
        listingsService.removePendingPurchase(listing)
        webApiService.sendOnListingsChangedEvent()
    }
}