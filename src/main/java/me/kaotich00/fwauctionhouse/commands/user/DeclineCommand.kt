package me.kaotich00.fwauctionhouse.commands.user

import com.google.inject.Inject
import me.kaotich00.fwauctionhouse.commands.api.UserCommand
import me.kaotich00.fwauctionhouse.message.Message
import me.kaotich00.fwauctionhouse.services.ListingsService
import me.kaotich00.fwauctionhouse.storage.ListingsDao
import me.kaotich00.fwauctionhouse.model.ListingStatus
import org.bukkit.entity.Player

class DeclineCommand @Inject constructor(
    private val listingsService: ListingsService,
    private val listingsDao: ListingsDao
) : UserCommand(
    "decline",
    "",
    1,
    "/market decline <listingId>"
) {
    override fun doCommand(sender: Player, args: Array<String>) {
        val listingId = args[1].toIntOrNull() ?: run {
            sender.sendMessage("You must insert a valid id")
            return
        }

        val pendingSell = listingsService.getPendingSell(listingId) ?: return

        Message.DECLINED.send(sender)
        listingsDao.updateListingStatus(pendingSell.listingId, ListingStatus.ORDER_AVAILABLE)
        listingsService.removeFromPendingSells(pendingSell)
    }
}