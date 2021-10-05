package me.kaotich00.fwauctionhouse.commands.api

import me.kaotich00.fwauctionhouse.message.Message
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

abstract class PlayerCommandHandler(
    override val name: String,
    override val requiredArgs: Int,
    override val usage: String,
    override val info: String
) : CommandHandler {

    abstract fun onCommand(sender: Player, args: Array<String>)

    final override fun onCommand(sender: CommandSender, args: Array<String>) {
        if (sender is Player) {
            onCommand(sender, args)
            return
        }

        Message.ONLY_PLAYERS_CAN_RUN_THIS_COMMAND.send(sender)
    }
}