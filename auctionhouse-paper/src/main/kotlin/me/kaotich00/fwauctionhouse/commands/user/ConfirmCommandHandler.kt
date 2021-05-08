package me.kaotich00.fwauctionhouse.commands.user

import com.google.inject.Inject
import me.kaotich00.fwauctionhouse.FwAuctionHouse
import me.kaotich00.fwauctionhouse.commands.api.PlayerCommandHandler
import me.kaotich00.fwauctionhouse.message.Message
import me.kaotich00.fwauctionhouse.model.itemStack
import me.kaotich00.fwauctionhouse.model.total
import me.kaotich00.fwauctionhouse.services.ListingsService
import me.kaotich00.fwauctionhouse.services.WebApiService
import me.kaotich00.fwauctionhouse.storage.ListingsDao
import me.kaotich00.fwauctionhouse.utils.launchAsync
import org.bukkit.Sound
import org.bukkit.entity.Player

class ConfirmCommandHandler @Inject constructor(
    private val listingsService: ListingsService,
    private val listingsDao: ListingsDao,
    private val webApiService: WebApiService
) : PlayerCommandHandler(
    name = "confirm",
    requiredArgs = 1,
    usage = "/market confirm <listingId>",
    info = "Confirm a purchase made on the web store"
) {

    override fun onCommand(sender: Player, args: Array<String>) {
        val listingId = args[1].toIntOrNull() ?: run {
            Message.INSERT_VALID_ID.send(sender)
            return
        }

        val listing = listingsService.getPendingPurchase(listingId) ?: return

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
            listingsService.removePendingPurchase(listing)
            listingsDao.deleteListing(listing.id.value)
            webApiService.sendOnListingsChangedEvent()
        }
    }

}