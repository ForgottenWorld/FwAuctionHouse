package me.kaotich00.fwauctionhouse.commands.api

import me.kaotich00.fwauctionhouse.utils.MessageUtils
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

abstract class UserCommand(
        override val name: String,
        override val info: String?,
        override val requiredArgs: Int,
        override val usage: String?
    ) : Command {

    final override fun onCommand(sender: CommandSender, args: Array<String>) {
        if (sender !is Player) {
            sender.sendMessage(MessageUtils.formatErrorMessage("Only players can run this command"))
            return
        }
        doCommand(sender, args)
    }

    protected abstract fun doCommand(sender: Player, args: Array<String>)

}