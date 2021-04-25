package me.kaotich00.fwauctionhouse.commands.admin

import com.google.inject.Inject
import me.kaotich00.fwauctionhouse.FwAuctionHouse
import me.kaotich00.fwauctionhouse.commands.api.AdminCommandHandler
import org.bukkit.entity.Player

class ReloadCommandHandler @Inject constructor(
    private val plugin: FwAuctionHouse
) : AdminCommandHandler(
    name = "reload",
    requiredArgs = 1,
    usage = "/market reload",
    info = "Reload the plugin's configuration"
) {

    override fun onAdminOnlyCommand(sender: Player, args: Array<String>) {
        plugin.reloadConfiguration()
    }
}