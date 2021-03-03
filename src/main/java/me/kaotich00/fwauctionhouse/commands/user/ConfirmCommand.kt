package me.kaotich00.fwauctionhouse.commands.user

import me.kaotich00.fwauctionhouse.FwAuctionHouse
import me.kaotich00.fwauctionhouse.commands.api.UserCommand
import me.kaotich00.fwauctionhouse.message.Message
import me.kaotich00.fwauctionhouse.objects.PendingSell
import me.kaotich00.fwauctionhouse.services.SimpleMarketService
import me.kaotich00.fwauctionhouse.storage.StorageFactory
import org.bukkit.Sound
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import java.util.*
import java.util.concurrent.CompletableFuture

class ConfirmCommand : UserCommand() {

    override fun onCommand(sender: CommandSender, args: Array<String>) {
        super.onCommand(sender, args)
        val buyer = sender as Player
        val listingId = args[1]
        val optPendingSell: Optional<PendingSell> =
            SimpleMarketService.getInstance()!!.getPendingSell(listingId.toInt())
        if (optPendingSell.isPresent) {
            val pendingSell = optPendingSell.get()
            val boughtItem = pendingSell.itemStack
            buyer.inventory.addItem(boughtItem)
            FwAuctionHouse.economy?.withdrawPlayer(buyer, pendingSell.totalCost.toDouble())
            Message.BOUGHT_ITEM.send(buyer, boughtItem!!.i18NDisplayName, boughtItem.amount, pendingSell.totalCost)
            buyer.playSound(buyer.location, Sound.ENTITY_PLAYER_LEVELUP, 1f, 1f)
            CompletableFuture.runAsync {
                SimpleMarketService.getInstance()!!.removeFromPendingSells(pendingSell)
                StorageFactory.instance?.storageMethod?.deletePendingSell(pendingSell.listingId)
            }
        }
    }

    override val info: String?
        get() = super.info

    override val usage: String?
        get() = "/market confirm <listingId>"

    override val name: String?
        get() = super.name

    override val requiredArgs: Int?
        get() = 1
}