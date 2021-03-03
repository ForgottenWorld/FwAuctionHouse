package me.kaotich00.fwauctionhouse.message

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
import me.kaotich00.fwauctionhouse.utils.MessageUtils
import net.milkbowl.vault.economy.Economy
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.command.CommandSender
import org.bukkit.plugin.java.JavaPlugin

enum class Message(message: String, showPrefix: Boolean) {
    PREFIX("fwauctionhouse_prefix", false), HELP_MESSAGE("help_message", false), SOLD_ITEM(
        "sold_item",
        true
    ),
    BOUGHT_ITEM("bought_item", true), INVENTORY_FULL("inventory_full", true), NOT_ENOUGH_MONEY(
        "not_enough_money",
        true
    ),
    CANNOT_SELL_AIR("cannot_sell_air", true), PURCHASE_MESSAGE(
        "purchase_message",
        false
    ),
    VALIDATED_TOKEN("validated_token", true), VALIDATED_TOKEN_MESSAGE(
        "validate_token_message",
        true
    ),
    DECLINED("declined", true);

    private val message: String?
    private val showPrefix: Boolean
    private val localizationManager: LocalizationManager?
    fun send(sender: CommandSender, vararg objects: Any?) {
        sender.sendMessage(asString(*objects))
    }

    fun broadcast(vararg objects: Any?) {
        Bukkit.getServer().broadcastMessage(asString(*objects))
    }

    fun asString(vararg objects: Any?): String {
        return format(*objects)
    }

    private fun format(vararg objects: Any): String {
        var string = localizationManager!!.localize(message)
        if (showPrefix) {
            string = localizationManager.localize(message) + " " + string
        }
        for (i in 0 until objects.size) {
            val o = objects[i]
            string = string!!.replace("{$i}", o.toString())
        }
        return ChatColor.translateAlternateColorCodes('&', string!!)
    }

    companion object {
        fun getLocalizationManager(): LocalizationManager? {
            return LocalizationManager.Companion.getInstance(
                JavaPlugin.getPlugin(
                    FwAuctionHouse::class.java
                )
            )
        }
    }

    init {
        this.message = MessageUtils.rewritePlaceholders(message)
        this.showPrefix = showPrefix
        localizationManager = FwAuctionHouse.Companion.getLocalizationManager()
    }
}