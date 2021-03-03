package me.kaotich00.fwauctionhouse.commands.api

import me.kaotich00.fwauctionhouse.utils.MessageUtils
import org.bukkit.command.CommandException
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

open class UserCommand : Command {

    override fun onCommand(sender: CommandSender, args: Array<String>) {
        if (sender !is Player) {
            sender.sendMessage(MessageUtils.formatErrorMessage("Only players can run this command"))
            throw CommandException()
        }
    }

    override val name: String?
        get() = null

    override val usage: String?
        get() = null

    override val info: String?
        get() = null

    override val requiredArgs: Int?
        get() = null

}