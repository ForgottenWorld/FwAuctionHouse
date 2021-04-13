package me.kaotich00.fwauctionhouse.commands.user

import com.google.inject.Inject
import me.kaotich00.fwauctionhouse.FwAuctionHouse
import me.kaotich00.fwauctionhouse.commands.api.UserCommand
import me.kaotich00.fwauctionhouse.message.Message
import me.kaotich00.fwauctionhouse.services.ListingsService
import me.kaotich00.fwauctionhouse.storage.ListingsDao
import me.kaotich00.fwauctionhouse.utils.launchAsync
import org.bukkit.Sound
import org.bukkit.entity.Player

class ConfirmCommand @Inject constructor(
    private val listingsService: ListingsService,
    private val listingsDao: ListingsDao
) : UserCommand(
    "confirm",
    "",
    1,
    "/market confirm <listingId>"
) {

    override fun doCommand(sender: Player, args: Array<String>) {
        val listingId = args[1].toIntOrNull() ?: run {
            sender.sendMessage("You must insert a valid id")
            return
        }

        val pendingSell = listingsService.getPendingSell(listingId) ?: return

        val boughtItem = pendingSell.itemStack

        sender.inventory.addItem(boughtItem)

        FwAuctionHouse.economy.withdrawPlayer(sender, pendingSell.totalCost)

        Message.BOUGHT_ITEM.send(
            sender,
            boughtItem.i18NDisplayName ?: "N/D",
            boughtItem.amount,
            pendingSell.totalCost
        )

        sender.playSound(sender.location, Sound.ENTITY_PLAYER_LEVELUP, 1f, 1f)

        launchAsync {
            listingsService.removeFromPendingSells(pendingSell)
            listingsDao.deletePendingSell(pendingSell.listingId)
        }
    }

}