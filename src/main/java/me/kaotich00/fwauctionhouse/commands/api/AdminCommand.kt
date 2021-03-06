package me.kaotich00.fwauctionhouse.commands.api

import me.kaotich00.fwauctionhouse.utils.MessageUtils
import org.bukkit.entity.Player

abstract class AdminCommand(
    name: String,
    info: String?,
    requiredArgs: Int,
    usage: String?
) : UserCommand(
    name,
    info,
    requiredArgs,
    usage
) {

    final override fun doCommand(sender: Player, args: Array<String>) {
        if (!sender.hasPermission("fwauctionhouse.admin")) {
            sender.sendMessage(MessageUtils.formatErrorMessage("You don't have permissions to run this command"))
            return
        }

        doAdminCommand(sender, args)
    }

    protected abstract fun doAdminCommand(sender: Player, args: Array<String>)

}