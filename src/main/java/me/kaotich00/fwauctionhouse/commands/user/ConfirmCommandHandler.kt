package me.kaotich00.fwauctionhouse.commands.user

import com.google.inject.Inject
import me.kaotich00.fwauctionhouse.FwAuctionHouse
import me.kaotich00.fwauctionhouse.commands.api.PlayerCommandHandler
import me.kaotich00.fwauctionhouse.message.Message
import me.kaotich00.fwauctionhouse.services.ListingsService
import me.kaotich00.fwauctionhouse.storage.ListingsDao
import me.kaotich00.fwauctionhouse.utils.launchAsync
import org.bukkit.Sound
import org.bukkit.entity.Player

class ConfirmCommandHandler @Inject constructor(
    private val listingsService: ListingsService,
    private val listingsDao: ListingsDao
) : PlayerCommandHandler(
    name = "confirm",
    requiredArgs = 1,
    usage = "/market confirm <listingId>"
) {

    override fun onCommand(sender: Player, args: Array<String>) {
        val listingId = args[1].toIntOrNull() ?: run {
            sender.sendMessage("You must insert a valid id")
            return
        }

        val listing = listingsService.getListing(listingId) ?: return

        val boughtItem = listing.itemStack

        sender.inventory.addItem(boughtItem)

        FwAuctionHouse.economy.withdrawPlayer(sender, listing.total.toDouble())

        Message.BOUGHT_ITEM.send(
            sender,
            boughtItem.i18NDisplayName ?: "N/D",
            boughtItem.amount,
            listing.total
        )

        sender.playSound(sender.location, Sound.ENTITY_PLAYER_LEVELUP, 1f, 1f)

        launchAsync {
            listingsService.removeFromListings(listing)
            listingsDao.deleteListing(listing.id.value)
        }
    }

}