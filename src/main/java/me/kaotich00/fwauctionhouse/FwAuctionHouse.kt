package me.kaotich00.fwauctionhouse

import me.kaotich00.fwauctionhouse.commands.MarketCommandManager
import me.kaotich00.fwauctionhouse.locale.LocalizationManager
import me.kaotich00.fwauctionhouse.services.SimpleMarketService
import me.kaotich00.fwauctionhouse.storage.StorageProvider
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
        LocalizationManager.getInstance(this)!!.loadLanguageFile()
        sender.sendMessage(ChatColor.GRAY.toString() + "   >> " + ChatColor.RESET + " Registering commands...")
        registerCommands()
        sender.sendMessage(ChatColor.GRAY.toString() + "   >> " + ChatColor.RESET + " Scheduling tasks...")
        scheduleTasks()
        sender.sendMessage(ChatColor.GRAY.toString() + "   >> " + ChatColor.RESET + " Registering economy...")
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

    private fun initStorage() {
        StorageProvider.storageInstance.init()
    }

    private fun shutdownStorage() {
        StorageProvider.storageInstance.shutdown()
    }

    private fun scheduleTasks() {
        SimpleMarketService.scheduleSellingTask()
        SimpleMarketService.scheduleConfirmTokenTask()
    }

    private fun registerCommands() {
        getCommand("market")!!.setExecutor(MarketCommandManager())
    }

    companion object {
        lateinit var defaultConfig: FileConfiguration

        val economy by lazy {
            Bukkit.getServer().servicesManager.getRegistration(
                Economy::class.java
            )?.provider ?: error("Economy service not present")
        }

        val localizationManager: LocalizationManager?
            get() = LocalizationManager.getInstance(
                getPlugin(
                    FwAuctionHouse::class.java
                )
            )
    }
}