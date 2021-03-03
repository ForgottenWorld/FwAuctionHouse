package me.kaotich00.fwauctionhouse.utils

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
import me.kaotich00.fwauctionhouse.utils.SerializationUtil
import me.kaotich00.fwauctionhouse.objects.PendingSell
import me.kaotich00.fwauctionhouse.objects.PendingToken
import java.sql.DriverManager
import me.kaotich00.fwauctionhouse.storage.StorageFactory
import me.kaotich00.fwauctionhouse.storage.sql.hikari.MySQLConnectionFactory
import me.kaotich00.fwauctionhouse.commands.api.UserCommand
import java.util.concurrent.CompletableFuture
import me.kaotich00.fwauctionhouse.services.SimpleMarketService
import java.lang.Runnable
import me.kaotich00.fwauctionhouse.utils.ListingStatus
import me.kaotich00.fwauctionhouse.utils.CommandUtils
import me.kaotich00.fwauctionhouse.commands.user.SellCommand
import me.kaotich00.fwauctionhouse.commands.user.ConfirmCommand
import me.kaotich00.fwauctionhouse.commands.user.DeclineCommand
import me.kaotich00.fwauctionhouse.commands.user.ValidateTokenCommand
import net.md_5.bungee.api.chat.ClickEvent
import net.md_5.bungee.api.chat.HoverEvent
import net.md_5.bungee.api.chat.ComponentBuilder
import java.util.HashSet
import me.kaotich00.fwauctionhouse.commands.MarketCommandManager
import net.milkbowl.vault.economy.Economy
import org.bukkit.ChatColor

object MessageUtils {
    var EOL = "\n"
    val pluginPrefix: String
        get() = ChatColor.DARK_GRAY.toString() + "[" +
                ChatColor.YELLOW + "Fw" +
                ChatColor.GOLD + ChatColor.BOLD + "War" +
                ChatColor.DARK_GRAY + "]"

    fun chatHeader(): String {
        return ChatColor.YELLOW.toString() + "oOo--------------------[ " +
                ChatColor.YELLOW + "Fw" +
                ChatColor.GOLD + ChatColor.BOLD + "War" +
                ChatColor.YELLOW + " ]-------------------oOo "
    }

    fun formatSuccessMessage(message: String): String {
        var message = message
        message = ChatColor.GREEN.toString() + message
        return message
    }

    fun formatErrorMessage(message: String?): String {
        var message = message
        message = ChatColor.RED.toString() + message
        return message
    }

    fun helpMessage(): String {
        var message = chatHeader()
        message = message +
                """
     
     ${ChatColor.GRAY}>> ${ChatColor.DARK_AQUA}/war ${ChatColor.AQUA}start 
     ${ChatColor.GRAY}>> ${ChatColor.DARK_AQUA}/war ${ChatColor.AQUA}plot 
     """.trimIndent()
        return message
    }

    fun rewritePlaceholders(input: String): String {
        var input = input
        var i = 0
        while (input.contains("{}")) {
            input = input.replaceFirst("\\{\\}".toRegex(), "{" + i++ + "}")
        }
        return input
    }
}