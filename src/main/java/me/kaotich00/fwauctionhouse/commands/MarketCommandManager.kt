package me.kaotich00.fwauctionhouse.commands

import com.google.inject.Inject
import me.kaotich00.fwauctionhouse.commands.api.Command
import me.kaotich00.fwauctionhouse.commands.user.ConfirmCommand
import me.kaotich00.fwauctionhouse.commands.user.DeclineCommand
import me.kaotich00.fwauctionhouse.commands.user.SellCommand
import me.kaotich00.fwauctionhouse.commands.user.ValidateTokenCommand
import me.kaotich00.fwauctionhouse.message.Message
import me.kaotich00.fwauctionhouse.utils.MessageUtils
import org.bukkit.command.CommandSender
import org.bukkit.command.TabExecutor

class MarketCommandManager @Inject constructor(
    sellCommand: SellCommand,
    confirmCommand: ConfirmCommand,
    declineCommand: DeclineCommand,
    validateTokenCommand: ValidateTokenCommand
) : TabExecutor {

    private val commandRegistry = mapOf<String, Command>(
        SELL_COMMAND to sellCommand,
        CONFIRM_COMMAND to confirmCommand,
        DECLINE_COMMAND to declineCommand,
        VALIDATE_TOKEN_COMMAND to validateTokenCommand
    )

    private fun getCommand(name: String) = commandRegistry[name]

    override fun onCommand(
        sender: CommandSender,
        command: org.bukkit.command.Command,
        label: String,
        args: Array<String>
    ): Boolean {
        if (args.isEmpty()) {
            Message.HELP_MESSAGE.send(sender)
            return true
        }

        val fwCommand = getCommand(args[0]) ?: run {
            Message.HELP_MESSAGE.send(sender)
            return true
        }

        if (fwCommand.requiredArgs < args.size) {
            fwCommand.onCommand(sender, args)
            return true
        }

        sender.sendMessage(MessageUtils.formatErrorMessage("Not enough arguments"))
        fwCommand.usage?.let { sender.sendMessage(MessageUtils.formatErrorMessage(it)) }
        return true
    }

    override fun onTabComplete(
        sender: CommandSender,
        command: org.bukkit.command.Command,
        alias: String,
        args: Array<String>
    ): List<String> {
        if (args.size != 1) return listOf()
        return commandRegistry.keys.filter { it.startsWith(args[0], ignoreCase = true) }
    }

    companion object {

        const val SELL_COMMAND = "sell"

        const val CONFIRM_COMMAND = "confirm"

        const val DECLINE_COMMAND = "decline"

        const val VALIDATE_TOKEN_COMMAND = "validateToken"
    }
}
