package me.kaotich00.fwauctionhouse.commands

import me.kaotich00.fwauctionhouse.commands.api.Command
import me.kaotich00.fwauctionhouse.commands.user.ConfirmCommand
import me.kaotich00.fwauctionhouse.commands.user.DeclineCommand
import me.kaotich00.fwauctionhouse.commands.user.SellCommand
import me.kaotich00.fwauctionhouse.commands.user.ValidateTokenCommand
import me.kaotich00.fwauctionhouse.message.Message
import me.kaotich00.fwauctionhouse.utils.CommandUtils
import me.kaotich00.fwauctionhouse.utils.MessageUtils
import me.kaotich00.fwauctionhouse.utils.NameUtil
import org.bukkit.command.CommandSender
import org.bukkit.command.TabExecutor
import java.util.*

class MarketCommandManager : TabExecutor {

    private val commandRegistry = mapOf<String, Command>(
        CommandUtils.SELL_COMMAND to SellCommand(),
        CommandUtils.CONFIRM_COMMAND to ConfirmCommand(),
        CommandUtils.DECLINE_COMMAND to DeclineCommand(),
        CommandUtils.VALIDATE_TOKEN_COMMAND to ValidateTokenCommand()
    )

    private fun getCommand(name: String): Command? {
        return commandRegistry[name]
    }

    override fun onCommand(
        sender: CommandSender,
        command: org.bukkit.command.Command,
        label: String,
        args: Array<String>
    ): Boolean {
        if (args.isEmpty()) {
            Message.HELP_MESSAGE.send(sender)
            return CommandUtils.COMMAND_SUCCESS
        }
        val fwCommand = getCommand(args[0])
        if (fwCommand != null) {
            if (fwCommand.requiredArgs > args.size) {
                sender.sendMessage(MessageUtils.formatErrorMessage("Not enough arguments"))
                sender.sendMessage(MessageUtils.formatErrorMessage(fwCommand.usage))
                return true
            }
            fwCommand.onCommand(sender, args)
        }
        return true
    }

    override fun onTabComplete(
        sender: CommandSender,
        command: org.bukkit.command.Command,
        alias: String,
        args: Array<String>
    ): List<String?> {
        val suggestions = mutableListOf<String>()
        var argsIndex = ""

        if (args.size == 1) {
            argsIndex = args[0]
            for (commandName in commandRegistry.keys) {
                suggestions.add(commandName)
            }
        }
        return NameUtil.filterByStart(suggestions, argsIndex)
    }

}