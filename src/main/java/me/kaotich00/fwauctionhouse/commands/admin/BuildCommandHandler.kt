package me.kaotich00.fwauctionhouse.commands.admin

import com.google.inject.Inject
import me.kaotich00.fwauctionhouse.commands.api.AdminCommandHandler
import me.kaotich00.fwauctionhouse.message.Message
import me.kaotich00.fwauctionhouse.services.MarketAreaService
import org.bukkit.entity.Player

class BuildCommandHandler @Inject constructor(
    private val marketAreaService: MarketAreaService
) : AdminCommandHandler(
    name = "build",
    requiredArgs = 1,
    usage = "/market build <enter/exit>",
) {

    override fun onAdminOnlyCommand(sender: Player, args: Array<String>) {
        when (args[1]) {
            "enter" -> marketAreaService.enterBuildMode(sender)
            "exit" -> marketAreaService.exitBuildMode(sender, false)
            else -> Message.POSSIBLE_OPTIONS_ENTER_EXIT.send(sender)
        }
    }
}