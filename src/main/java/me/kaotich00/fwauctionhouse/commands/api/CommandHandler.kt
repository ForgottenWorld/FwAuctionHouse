package me.kaotich00.fwauctionhouse.commands.api

import org.bukkit.command.CommandSender

interface CommandHandler {

    val name: String

    val usage: String

    val requiredArgs: Int


    fun onCommand(sender: CommandSender, args: Array<String>)
}