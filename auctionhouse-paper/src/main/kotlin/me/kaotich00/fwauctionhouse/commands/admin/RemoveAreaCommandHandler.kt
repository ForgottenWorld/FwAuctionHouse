package me.kaotich00.fwauctionhouse.commands.admin

import com.google.inject.Inject
import me.kaotich00.fwauctionhouse.commands.api.AdminCommandHandler
import me.kaotich00.fwauctionhouse.message.Message
import me.kaotich00.fwauctionhouse.services.MarketAreaService
import org.bukkit.entity.Player

class RemoveAreaCommandHandler @Inject constructor(
    private val marketAreaService: MarketAreaService
) : AdminCommandHandler(
    name = "build",
    requiredArgs = 1,
    usage = "/market removearea [<id>, this]",
    info = "Remove a market area"
) {

    override fun onAdminOnlyCommand(sender: Player, args: Array<String>) {
        val option = args.getOrNull(0) ?: return

        if (option.equals("this", true)) {
            val marketArea = marketAreaService.getMarketAreaPlayerIsIn(sender) ?: run {
                Message.NO_SUCH_MARKET_AREA.send(sender)
                return
            }
            marketAreaService.removeMarketArea(marketArea)
            Message.MARKET_AREA_REMOVED.send(sender)
            return
        }

        val id = option.toIntOrNull() ?: run {
            Message.POSSIBLE_OPTIONS_ID_THIS.send(sender)
            return
        }

        val marketArea = marketAreaService.getMarketAreaById(id) ?: run {
            Message.NO_SUCH_MARKET_AREA.send(sender)
            return
        }

        marketAreaService.removeMarketArea(marketArea)
        Message.MARKET_AREA_REMOVED.send(sender)

        Message.MARKET_AREA_REMOVED.send(sender)
    }
}