package me.kaotich00.fwauctionhouse.commands

import com.google.inject.Inject
import me.kaotich00.fwauctionhouse.commands.admin.BuildCommandHandler
import me.kaotich00.fwauctionhouse.commands.admin.PosCommandHandler
import me.kaotich00.fwauctionhouse.commands.admin.ReloadCommandHandler
import me.kaotich00.fwauctionhouse.commands.api.CommandHandler
import me.kaotich00.fwauctionhouse.commands.user.ConfirmCommandHandler
import me.kaotich00.fwauctionhouse.commands.user.DeclineCommandHandler
import me.kaotich00.fwauctionhouse.commands.user.SellCommandHandler
import me.kaotich00.fwauctionhouse.commands.user.ValidateTokenCommandHandler
import me.kaotich00.fwauctionhouse.message.Message
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.command.TabExecutor

class MarketCommandManager @Inject constructor(
    sellCommand: SellCommandHandler,
    confirmCommand: ConfirmCommandHandler,
    declineCommand: DeclineCommandHandler,
    validateTokenCommand: ValidateTokenCommandHandler,
    reloadCommand: ReloadCommandHandler,
    buildCommand: BuildCommandHandler,
    pos1Command: PosCommandHandler.One,
    pos2Command: PosCommandHandler.Two
) : TabExecutor {

    private val commandRegistry = arrayOf(
        sellCommand,
        confirmCommand,
        declineCommand,
        validateTokenCommand,
        reloadCommand,
        buildCommand,
        pos1Command,
        pos2Command,
    ).associateBy { it.name }

    private fun getCommandHandler(name: String) = commandRegistry[name]

    override fun onCommand(
        sender: CommandSender,
        command: Command,
        label: String,
        args: Array<String>
    ): Boolean {
        if (args.isEmpty()) {
            Message.HELP_MESSAGE.send(sender)
            return true
        }

        val handler = getCommandHandler(args[0]) ?: run {
            Message.HELP_MESSAGE.send(sender)
            return true
        }

        if (handler.requiredArgs < args.size) {
            handler.onCommand(sender, args)
            return true
        }

        sender.sendMessage(Component.text("Not enough arguments", NamedTextColor.RED))
        sender.sendMessage(Component.text(handler.usage))
        return true
    }

    override fun onTabComplete(
        sender: CommandSender,
        command: Command,
        alias: String,
        args: Array<String>
    ): List<String> {
        if (args.size != 1) return listOf()
        return commandRegistry.keys.filter { it.startsWith(args[0], ignoreCase = true) }
    }
}
