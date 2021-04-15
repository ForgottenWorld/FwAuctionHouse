package me.kaotich00.fwauctionhouse.commands.api

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.entity.Player

abstract class AdminCommandHandler(
    name: String,
    requiredArgs: Int,
    usage: String
) : PlayerCommandHandler(name, requiredArgs, usage) {

    final override fun onCommand(sender: Player, args: Array<String>) {
        if (!sender.hasPermission("fwauctionhouse.admin")) {
            sender.sendMessage(
                Component.text(
                    "You don't have permissions to run this command",
                    NamedTextColor.RED
                )
            )
            return
        }

        onAdminOnlyCommand(sender, args)
    }

    protected abstract fun onAdminOnlyCommand(sender: Player, args: Array<String>)
}