package me.kaotich00.fwauctionhouse.commands.user

import me.kaotich00.fwauctionhouse.commands.api.UserCommand
import me.kaotich00.fwauctionhouse.message.Message
import me.kaotich00.fwauctionhouse.objects.PendingToken
import me.kaotich00.fwauctionhouse.services.SimpleMarketService
import me.kaotich00.fwauctionhouse.storage.StorageFactory
import org.bukkit.command.CommandSender
import java.util.*

class ValidateTokenCommand : UserCommand() {

    override fun onCommand(sender: CommandSender, args: Array<String>) {
        super.onCommand(sender, args)
        val sessionId = args[1]
        val optPendingToken: Optional<PendingToken> =
            SimpleMarketService.getInstance()!!.getPendingToken(sessionId.toInt())
        if (optPendingToken.isPresent) {
            val pendingSell = optPendingToken.get()
            Message.VALIDATED_TOKEN.send(sender)
            StorageFactory.instance?.storageMethod?.validateToken(sessionId.toInt())
            SimpleMarketService.getInstance()!!.removeFromPendingToken(pendingSell)
        }
    }

    override val info: String?
        get() = super.info

    override val usage: String?
        get() = "/market validateToken <sessionId>"

    override val name: String?
        get() = super.name
    
    override val requiredArgs: Int?
        get() = 1
}