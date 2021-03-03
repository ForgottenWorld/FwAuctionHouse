package me.kaotich00.fwauctionhouse.locale

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
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.configuration.file.YamlConfiguration
import java.io.File

class LocalizationManager private constructor(plugin: FwAuctionHouse) {
    private val plugin: FwAuctionHouse
    private val defaultLanguageFile = "language_en_EN.yml"
    private val strings: MutableMap<String?, String>
    fun initDefaultLocalization() {
        plugin.saveResource(defaultLanguageFile, true)
    }

    fun loadLanguageFile() {
        val defaultConfig: FileConfiguration = FwAuctionHouse.Companion.getDefaultConfig()
        initDefaultLocalization()
        var localizationFile = defaultConfig.getString("localization.language_file")
        if (localizationFile == null) {
            localizationFile = defaultLanguageFile
        }
        val data: FileConfiguration = YamlConfiguration.loadConfiguration(File(plugin.dataFolder, localizationFile))
        for (key in data.getConfigurationSection("strings")!!.getKeys(false)) {
            strings[key] = data.getString("strings.$key")!!
        }
    }

    fun reload() {
        strings.clear()
        loadLanguageFile()
    }

    fun localize(key: String?): String? {
        return if (strings.containsKey(key)) strings[key] else ChatColor.RED.toString() + "No translation present for " + key
    }

    companion object {
        private var instance: LocalizationManager? = null
        fun getInstance(plugin: FwAuctionHouse): LocalizationManager? {
            if (instance == null) {
                instance = LocalizationManager(plugin)
            }
            return instance
        }
    }

    init {
        if (instance != null) {
            throw RuntimeException("Use getInstance() method to get the single instance of this class.")
        }
        strings = HashMap()
        this.plugin = plugin
    }
}