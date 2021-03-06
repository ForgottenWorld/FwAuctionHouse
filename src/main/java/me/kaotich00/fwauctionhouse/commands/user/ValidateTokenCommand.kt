package me.kaotich00.fwauctionhouse.commands.user

import me.kaotich00.fwauctionhouse.commands.api.UserCommand
import me.kaotich00.fwauctionhouse.message.Message
import me.kaotich00.fwauctionhouse.services.SimpleMarketService
import me.kaotich00.fwauctionhouse.storage.StorageProvider
import org.bukkit.entity.Player

class ValidateTokenCommand : UserCommand(
    "validateToken",
    "",
    1,
    "/market validateToken <sessionId>"
) {

    override fun doCommand(sender: Player, args: Array<String>) {

        val sessionId = args[1].toIntOrNull() ?: run {
            sender.sendMessage("You must insert a valid session id")
            return
        }

        val pendingToken = SimpleMarketService.getPendingToken(sessionId) ?: return

        Message.VALIDATED_TOKEN.send(sender)
        StorageProvider.storageInstance.storageMethod.validateToken(sessionId)
        SimpleMarketService.removeFromPendingToken(pendingToken)
    }

}