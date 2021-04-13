package me.kaotich00.fwauctionhouse.commands.user

import com.google.inject.Inject
import me.kaotich00.fwauctionhouse.commands.api.UserCommand
import me.kaotich00.fwauctionhouse.message.Message
import me.kaotich00.fwauctionhouse.services.ListingsService
import me.kaotich00.fwauctionhouse.storage.ListingsDao
import org.bukkit.entity.Player

class ValidateTokenCommand @Inject constructor(
    private val listingsService: ListingsService,
    private val listingsDao: ListingsDao
) : UserCommand(
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

        val pendingToken = listingsService.getPendingToken(sessionId) ?: return

        Message.VALIDATED_TOKEN.send(sender)
        listingsDao.validateToken(sessionId)
        listingsService.removeFromPendingToken(pendingToken)
    }

}