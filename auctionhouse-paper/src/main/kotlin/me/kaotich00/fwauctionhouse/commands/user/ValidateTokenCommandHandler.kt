package me.kaotich00.fwauctionhouse.commands.user

import com.google.inject.Inject
import me.kaotich00.fwauctionhouse.commands.api.PlayerCommandHandler
import me.kaotich00.fwauctionhouse.message.Message
import me.kaotich00.fwauctionhouse.services.ListingsService
import me.kaotich00.fwauctionhouse.services.WebApiService
import me.kaotich00.fwauctionhouse.storage.ListingsDao
import org.bukkit.entity.Player

class ValidateTokenCommandHandler @Inject constructor(
    private val listingsService: ListingsService,
    private val listingsDao: ListingsDao,
    private val webApiService: WebApiService
) : PlayerCommandHandler(
    name = "validateToken",
    requiredArgs = 1,
    usage = "/market validateToken <sessionId>",
    info = "Validate a web store session token"
) {

    override fun onCommand(sender: Player, args: Array<String>) {
        val sessionId = args[1].toIntOrNull() ?: run {
            Message.INSERT_VALID_SESSION_ID.send(sender)
            return
        }

        val playerSession = listingsService.getPlayerSession(sessionId) ?: return

        Message.VALIDATED_TOKEN.send(sender)
        listingsDao.validateToken(sessionId)
        listingsService.removeFromPlayerSession(playerSession)
        webApiService.sendOnTokenConfirmedEvent(playerSession.token)
    }

}