package me.kaotich00.fwauctionhouse.commands.user

import me.kaotich00.fwauctionhouse.commands.api.UserCommand
import me.kaotich00.fwauctionhouse.message.Message
import me.kaotich00.fwauctionhouse.objects.PendingSell
import me.kaotich00.fwauctionhouse.services.SimpleMarketService
import me.kaotich00.fwauctionhouse.storage.StorageProvider
import me.kaotich00.fwauctionhouse.utils.ListingStatus
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import java.util.*

class DeclineCommand : UserCommand(
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

        val pendingSell = SimpleMarketService.getPendingSell(listingId)

        if(pendingSell != null) {
            Message.DECLINED.send(sender)
            StorageProvider.storageInstance.storageMethod.updateListingStatus(
                pendingSell.listingId,
                ListingStatus.ORDER_AVAILABLE
            )
            SimpleMarketService.removeFromPendingSells(pendingSell)
        }
    }
}