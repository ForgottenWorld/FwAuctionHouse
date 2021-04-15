package me.kaotich00.fwauctionhouse.commands.user

import com.google.inject.Inject
import me.kaotich00.fwauctionhouse.commands.api.PlayerCommandHandler
import me.kaotich00.fwauctionhouse.message.Message
import me.kaotich00.fwauctionhouse.services.ListingsService
import me.kaotich00.fwauctionhouse.storage.ListingsDao
import org.bukkit.entity.Player

class ValidateTokenCommandHandler @Inject constructor(
    private val listingsService: ListingsService,
    private val listingsDao: ListingsDao
) : PlayerCommandHandler(
    name = "validateToken",
    requiredArgs = 1,
    usage = "/market validateToken <sessionId>"
) {

    override fun onCommand(sender: Player, args: Array<String>) {
        val sessionId = args[1].toIntOrNull() ?: run {
            sender.sendMessage("You must insert a valid session id")
            return
        }

        val playerSession = listingsService.getPlayerSession(sessionId) ?: return

        Message.VALIDATED_TOKEN.send(sender)
        listingsDao.validateToken(sessionId)
        listingsService.removeFromPlayerSession(playerSession)
    }

}