package me.kaotich00.fwauctionhouse.commands.user

import com.google.inject.Inject
import me.kaotich00.fwauctionhouse.commands.api.PlayerCommandHandler
import me.kaotich00.fwauctionhouse.message.Message
import me.kaotich00.fwauctionhouse.services.ListingsService
import me.kaotich00.fwauctionhouse.storage.ListingsDao
import me.kaotich00.fwauctionhouse.model.listing.ListingStatus
import org.bukkit.entity.Player

class DeclineCommandHandler @Inject constructor(
    private val listingsService: ListingsService,
    private val listingsDao: ListingsDao
) : PlayerCommandHandler(
    name = "decline",
    requiredArgs = 1,
    usage = "/market decline <listingId>"
) {
    override fun onCommand(sender: Player, args: Array<String>) {
        val listingId = args[1].toIntOrNull() ?: run {
            sender.sendMessage("You must insert a valid id")
            return
        }

        val listing = listingsService.getListing(listingId) ?: return

        Message.DECLINED.send(sender)
        listingsDao.updateListingStatus(listing.id.value, ListingStatus.ORDER_AVAILABLE)
        listingsService.removeFromListings(listing)
    }
}