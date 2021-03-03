package me.kaotich00.fwauctionhouse.commands.user

import me.kaotich00.fwauctionhouse.commands.api.UserCommand
import me.kaotich00.fwauctionhouse.message.Message
import me.kaotich00.fwauctionhouse.objects.PendingSell
import me.kaotich00.fwauctionhouse.services.SimpleMarketService
import me.kaotich00.fwauctionhouse.storage.StorageFactory
import me.kaotich00.fwauctionhouse.utils.ListingStatus
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import java.util.*

class DeclineCommand : UserCommand() {
    override fun onCommand(sender: CommandSender, args: Array<String>) {
        super.onCommand(sender, args)
        val buyer = sender as Player
        val listingId = args[1]
        val optPendingSell: Optional<PendingSell> =
            SimpleMarketService.getInstance()!!.getPendingSell(listingId.toInt())
        if (optPendingSell.isPresent) {
            val pendingSell = optPendingSell.get()
            Message.DECLINED.send(sender)
            StorageFactory.instance?.storageMethod?.updateListingStatus(
                pendingSell.listingId,
                ListingStatus.ORDER_AVAILABLE
            )
            SimpleMarketService.getInstance()!!.removeFromPendingSells(pendingSell)
        }
    }

    override val info: String?
        get() = super.info

    override val usage: String?
        get() = "/market decline <listingId>"

    override val name: String?
        get() = super.name

    override val requiredArgs: Int?
        get() = 1
}