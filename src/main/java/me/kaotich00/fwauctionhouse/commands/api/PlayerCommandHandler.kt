package me.kaotich00.fwauctionhouse.commands.api

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

abstract class PlayerCommandHandler(
    override val name: String,
    override val requiredArgs: Int,
    override val usage: String
) : CommandHandler {

    abstract fun onCommand(sender: Player, args: Array<String>)

    final override fun onCommand(sender: CommandSender, args: Array<String>) {
        if (sender !is Player) {
            sender.sendMessage(Component.text("Only players can run this command", NamedTextColor.RED))
            return
        }
        onCommand(sender, args)
    }
}