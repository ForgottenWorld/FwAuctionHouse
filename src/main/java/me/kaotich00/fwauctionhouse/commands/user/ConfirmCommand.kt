package me.kaotich00.fwauctionhouse.commands.user

import me.kaotich00.fwauctionhouse.FwAuctionHouse
import me.kaotich00.fwauctionhouse.commands.api.UserCommand
import me.kaotich00.fwauctionhouse.message.Message
import me.kaotich00.fwauctionhouse.objects.PendingSell
import me.kaotich00.fwauctionhouse.services.SimpleMarketService
import me.kaotich00.fwauctionhouse.storage.StorageFactory
import org.bukkit.Sound
import org.bukkit.entity.Player
import java.util.*
import java.util.concurrent.CompletableFuture

class ConfirmCommand : UserCommand(
   "confirm",
   "",
   1,
    "/market confirm <listingId>"
) {

    override fun doCommand(sender: Player, args: Array<String>) {
        val listingId = args[1]
        val optPendingSell: Optional<PendingSell> =
            SimpleMarketService.getInstance()!!.getPendingSell(listingId.toInt())
        if (optPendingSell.isPresent) {
            val pendingSell = optPendingSell.get()
            val boughtItem = pendingSell.itemStack
            sender.inventory.addItem(boughtItem)
            FwAuctionHouse.economy?.withdrawPlayer(sender, pendingSell.totalCost.toDouble())
            Message.BOUGHT_ITEM.send(sender, boughtItem!!.i18NDisplayName, boughtItem.amount, pendingSell.totalCost)
            sender.playSound(sender.location, Sound.ENTITY_PLAYER_LEVELUP, 1f, 1f)
            CompletableFuture.runAsync {
                SimpleMarketService.getInstance()!!.removeFromPendingSells(pendingSell)
                StorageFactory.instance?.storageMethod?.deletePendingSell(pendingSell.listingId)
            }
        }
    }

}