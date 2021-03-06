package me.kaotich00.fwauctionhouse.commands.user

import me.kaotich00.fwauctionhouse.FwAuctionHouse
import me.kaotich00.fwauctionhouse.commands.api.UserCommand
import me.kaotich00.fwauctionhouse.message.Message
import me.kaotich00.fwauctionhouse.services.SimpleMarketService
import me.kaotich00.fwauctionhouse.storage.StorageProvider
import org.bukkit.Sound
import org.bukkit.entity.Player
import java.util.concurrent.CompletableFuture

class ConfirmCommand : UserCommand(
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

        val pendingSell = SimpleMarketService.getPendingSell(listingId)

        if (pendingSell != null) {
            val boughtItem = pendingSell.itemStack
            sender.inventory.addItem(boughtItem)
            FwAuctionHouse.economy.withdrawPlayer(sender, pendingSell.totalCost.toDouble())
            Message.BOUGHT_ITEM.send(sender, boughtItem!!.i18NDisplayName, boughtItem.amount, pendingSell.totalCost)
            sender.playSound(sender.location, Sound.ENTITY_PLAYER_LEVELUP, 1f, 1f)
            CompletableFuture.runAsync {
                SimpleMarketService.removeFromPendingSells(pendingSell)
                StorageProvider.storageInstance.storageMethod.deletePendingSell(pendingSell.listingId)
            }
        }
    }

}