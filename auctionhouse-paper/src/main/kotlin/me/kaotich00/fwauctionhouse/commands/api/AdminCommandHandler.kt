package me.kaotich00.fwauctionhouse.commands.api

import me.kaotich00.fwauctionhouse.message.Message
import org.bukkit.entity.Player

abstract class AdminCommandHandler(
    name: String,
    requiredArgs: Int,
    usage: String,
    info: String
) : PlayerCommandHandler(name, requiredArgs, usage, info) {

    final override fun onCommand(sender: Player, args: Array<String>) {
        if (sender.hasPermission("fwauctionhouse.admin")) {
            onAdminOnlyCommand(sender, args)
            return
        }

        sender.sendMessage(Message.YOU_DONT_HAVE_PERMISSION_TO_USE_COMMAND.asComponent())
    }

    protected abstract fun onAdminOnlyCommand(sender: Player, args: Array<String>)
}