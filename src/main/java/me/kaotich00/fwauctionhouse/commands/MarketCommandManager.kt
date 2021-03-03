package me.kaotich00.fwauctionhouse.commands

import java.util.stream.Collectors
import kotlin.Throws
import java.lang.IllegalStateException
import java.io.ByteArrayOutputStream
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder
import java.lang.Exception
import java.io.IOException
import java.io.ByteArrayInputStream
import java.lang.ClassNotFoundException
import me.kaotich00.fwauctionhouse.FwAuctionHouse
import me.kaotich00.fwauctionhouse.locale.LocalizationManager
import java.lang.RuntimeException
import java.util.HashMap
import me.kaotich00.fwauctionhouse.storage.util.StorageCredentials
import me.kaotich00.fwauctionhouse.storage.sql.hikari.HikariConnectionFactory
import com.zaxxer.hikari.HikariConfig
import me.kaotich00.fwauctionhouse.storage.sql.ConnectionFactory
import com.zaxxer.hikari.HikariDataSource
import java.lang.LinkageError
import java.sql.SQLException
import me.kaotich00.fwauctionhouse.storage.StorageMethod
import java.sql.PreparedStatement
import me.kaotich00.fwauctionhouse.storage.sql.SqlStorage
import me.kaotich00.fwauctionhouse.objects.PendingSell
import me.kaotich00.fwauctionhouse.objects.PendingToken
import java.sql.DriverManager
import me.kaotich00.fwauctionhouse.storage.StorageFactory
import me.kaotich00.fwauctionhouse.storage.sql.hikari.MySQLConnectionFactory
import me.kaotich00.fwauctionhouse.commands.api.UserCommand
import java.util.concurrent.CompletableFuture
import me.kaotich00.fwauctionhouse.services.SimpleMarketService
import java.lang.Runnable
import me.kaotich00.fwauctionhouse.commands.user.SellCommand
import me.kaotich00.fwauctionhouse.commands.user.ConfirmCommand
import me.kaotich00.fwauctionhouse.commands.user.DeclineCommand
import me.kaotich00.fwauctionhouse.commands.user.ValidateTokenCommand
import net.md_5.bungee.api.chat.ClickEvent
import net.md_5.bungee.api.chat.HoverEvent
import net.md_5.bungee.api.chat.ComponentBuilder
import java.util.HashSet
import me.kaotich00.fwauctionhouse.commands.MarketCommandManager
import me.kaotich00.fwauctionhouse.commands.api.Command
import me.kaotich00.fwauctionhouse.message.Message
import me.kaotich00.fwauctionhouse.utils.*
import net.milkbowl.vault.economy.Economy
import org.bukkit.command.CommandException
import org.bukkit.command.CommandSender
import org.bukkit.command.TabExecutor
import java.util.ArrayList

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
        if (args.size == 0) {
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
    ): List<String>? {
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