package me.kaotich00.fwauctionhouse.commands

import com.google.inject.Inject
import me.kaotich00.fwauctionhouse.commands.admin.BuildCommandHandler
import me.kaotich00.fwauctionhouse.commands.admin.PosCommandHandler
import me.kaotich00.fwauctionhouse.commands.admin.ReloadCommandHandler
import me.kaotich00.fwauctionhouse.commands.admin.RemoveAreaCommandHandler
import me.kaotich00.fwauctionhouse.commands.api.AdminCommandHandler
import me.kaotich00.fwauctionhouse.commands.user.*
import me.kaotich00.fwauctionhouse.message.Message
import me.kaotich00.fwauctionhouse.message.TextComponents
import me.kaotich00.fwauctionhouse.message.send
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
    pos2Command: PosCommandHandler.Two,
    logoutCommand: LogoutCommandHandler,
    removeAreaCommand: RemoveAreaCommandHandler
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
        logoutCommand,
        removeAreaCommand
    ).associateBy { it.name }

    private val adminHelpComponent by lazy {
        commandRegistry
            .values
            .fold(Message.HELP_MESSAGE.asComponent()) { acc, c ->
                acc.append(TextComponents.newLine)
                    .append(Component.text(c.usage))
                    .append(Component.text(" ~ ", NamedTextColor.GRAY))
                    .append(Component.text(c.info))
            }
    }

    private val publicHelpComponent by lazy {
        commandRegistry
            .values
            .filter { it !is AdminCommandHandler }
            .fold(Message.HELP_MESSAGE.asComponent()) { acc, c ->
                acc.append(TextComponents.newLine)
                    .append(Component.text(c.usage))
                    .append(Component.text(" ~ ", NamedTextColor.GRAY))
                    .append(Component.text(c.info))
            }
    }

    private fun sendHelpComponent(sender: CommandSender) {
        if (sender.hasPermission("market.admin")) {
            sender.sendMessage(adminHelpComponent)
        } else {
            sender.sendMessage(publicHelpComponent)
        }
    }

    private fun getCommandHandler(name: String) = commandRegistry[name]

    override fun onCommand(
        sender: CommandSender,
        command: Command,
        label: String,
        args: Array<String>
    ): Boolean {
        if (args.isEmpty()) {
            sendHelpComponent(sender)
            return true
        }

        val handler = getCommandHandler(args[0]) ?: run {
            sendHelpComponent(sender)
            return true
        }

        if (handler.requiredArgs < args.size) {
            handler.onCommand(sender, args)
            return true
        }

        Message.NOT_ENOUGH_ARGUMENTS.send(sender)
        Component.text(handler.usage).send(sender)
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
