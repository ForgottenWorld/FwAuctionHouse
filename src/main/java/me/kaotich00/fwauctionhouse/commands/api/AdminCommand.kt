package me.kaotich00.fwauctionhouse.commands.api

import me.kaotich00.fwauctionhouse.utils.MessageUtils
import org.bukkit.command.CommandException
import org.bukkit.command.CommandSender

class AdminCommand : UserCommand() {
    override fun onCommand(sender: CommandSender, args: Array<String>) {
        if (!sender.hasPermission("fwauctionhouse.admin")) {
            sender.sendMessage(MessageUtils.formatErrorMessage("You don't have permissions to run this command"))
            throw CommandException()
        }
    }
}