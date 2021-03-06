package me.kaotich00.fwauctionhouse.commands

import me.kaotich00.fwauctionhouse.FwAuctionHouse
import me.kaotich00.fwauctionhouse.commands.api.Command
import me.kaotich00.fwauctionhouse.commands.user.DeclineCommand
import me.kaotich00.fwauctionhouse.commands.user.SellCommand
import me.kaotich00.fwauctionhouse.commands.user.ValidateTokenCommand
import me.kaotich00.fwauctionhouse.message.Message
import me.kaotich00.fwauctionhouse.utils.CommandUtils
import me.kaotich00.fwauctionhouse.utils.MessageUtils
import me.kaotich00.fwauctionhouse.utils.NameUtil
import org.bukkit.command.CommandException
import org.bukkit.command.CommandSender
import org.bukkit.command.TabExecutor
import java.util.*

class MarketCommandManager(plugin: FwAuctionHouse) : TabExecutor {

    private val commandRegistry: MutableMap<String?, Command>
    private val plugin: FwAuctionHouse

    private fun setup() {
        commandRegistry[CommandUtils.SELL_COMMAND] = SellCommand()
        commandRegistry[CommandUtils.CONFIRM_COMMAND] = ConfirmCommand()
        commandRegistry[CommandUtils.DECLINE_COMMAND] = DeclineCommand()
        commandRegistry[CommandUtils.VALIDATE_TOKEN_COMMAND] = ValidateTokenCommand()
    }

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
            if (fwCommand.requiredArgs!! > args.size) {
                sender.sendMessage(MessageUtils.formatErrorMessage("Not enough arguments"))
                sender.sendMessage(MessageUtils.formatErrorMessage(fwCommand.usage))
                return true
            }
            try {
                fwCommand.onCommand(sender, args)
            } catch (e: CommandException) {
            }
        }
        return true
    }

    override fun onTabComplete(
        sender: CommandSender,
        command: org.bukkit.command.Command,
        alias: String,
        args: Array<String>
    ): List<String?> {
        val suggestions: MutableList<String?> = ArrayList()
        var argsIndex = ""

        /* Suggest child commands */if (args.size == 1) {
            argsIndex = args[0]
            for (commandName in commandRegistry.keys) {
                suggestions.add(commandName)
            }
        }
        return NameUtil.filterByStart(suggestions, argsIndex)
    }

    init {
        commandRegistry = HashMap()
        this.plugin = plugin
        setup()
    }
}