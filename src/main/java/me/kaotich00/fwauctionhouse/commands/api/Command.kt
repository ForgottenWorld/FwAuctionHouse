package me.kaotich00.fwauctionhouse.commands.api

import org.bukkit.command.CommandSender

interface Command {
    fun onCommand(sender: CommandSender, args: Array<String>)
    val name: String?
    val usage: String?
    val info: String?
    val requiredArgs: Int?
}