package me.kaotich00.fwauctionhouse.commands.api

import org.bukkit.command.CommandSender

interface Command {

    val name: String

    val usage: String?

    val info: String?

    val requiredArgs: Int


    fun onCommand(sender: CommandSender, args: Array<String>)
}