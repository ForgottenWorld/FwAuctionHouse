package me.kaotich00.fwauctionhouse

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
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.plugin.java.JavaPlugin

class FwAuctionHouse : JavaPlugin() {
    override fun onEnable() {
        val sender = Bukkit.getConsoleSender()
        sender.sendMessage(ChatColor.DARK_GRAY.toString() + "" + ChatColor.STRIKETHROUGH + "=====================[" + ChatColor.GRAY + " Fw" + ChatColor.GREEN + "Market " + ChatColor.DARK_GRAY + "]======================")
        sender.sendMessage(ChatColor.GRAY.toString() + "   >> " + ChatColor.RESET + " Loading configuration...")
        loadConfiguration()
        sender.sendMessage(ChatColor.GRAY.toString() + "   >> " + ChatColor.RESET + " Initializing database...")
        initStorage()
        sender.sendMessage(ChatColor.GRAY.toString() + "   >> " + ChatColor.RESET + " Loading localization...")
        LocalizationManager.Companion.getInstance(this)!!.loadLanguageFile()
        sender.sendMessage(ChatColor.GRAY.toString() + "   >> " + ChatColor.RESET + " Registering commands...")
        registerCommands()
        sender.sendMessage(ChatColor.GRAY.toString() + "   >> " + ChatColor.RESET + " Scheduling tasks...")
        scheduleTasks()
        sender.sendMessage(ChatColor.GRAY.toString() + "   >> " + ChatColor.RESET + " Registering economy...")
        if (!setupEconomy()) {
            logger.severe("This plugin needs Vault and an Economy plugin in order to function!")
            Bukkit.getPluginManager().disablePlugin(this)
        }
        sender.sendMessage(ChatColor.DARK_GRAY.toString() + "" + ChatColor.STRIKETHROUGH + "====================================================")
    }

    override fun onDisable() {
        shutdownStorage()
    }

    private fun loadConfiguration() {
        config.options().copyDefaults(true)
        saveDefaultConfig()
        defaultConfig = config
    }

    fun reloadDefaultConfig() {
        reloadConfig()
        defaultConfig = config
    }

    fun initStorage() {
        val storage = StorageFactory.getInstance()
        storage!!.init()
    }

    fun shutdownStorage() {
        val storage = StorageFactory.getInstance()
        storage!!.shutdown()
    }

    fun scheduleTasks() {
        SimpleMarketService.Companion.getInstance()!!.scheduleSellingTask()
        SimpleMarketService.Companion.getInstance()!!.scheduleConfirmTokenTask()
    }

    fun registerCommands() {
        getCommand("market")!!.setExecutor(MarketCommandManager(this))
    }

    fun setupEconomy(): Boolean {
        if (Bukkit.getPluginManager().getPlugin("Vault") == null) {
            return false
        }
        val rsp = server.servicesManager.getRegistration(
            Economy::class.java
        ) ?: return false
        economy = rsp.provider
        return economy != null
    }

    companion object {
        var defaultConfig: FileConfiguration? = null
        var economy: Economy? = null
        val localizationManager: LocalizationManager?
            get() = LocalizationManager.Companion.getInstance(
                getPlugin(
                    FwAuctionHouse::class.java
                )
            )
    }
}