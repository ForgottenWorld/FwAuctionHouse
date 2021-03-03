package me.kaotich00.fwauctionhouse

import me.kaotich00.fwauctionhouse.commands.MarketCommandManager
import me.kaotich00.fwauctionhouse.locale.LocalizationManager
import me.kaotich00.fwauctionhouse.services.SimpleMarketService
import me.kaotich00.fwauctionhouse.storage.StorageFactory
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

    private fun initStorage() {
        val storage = StorageFactory.instance
        storage!!.init()
    }

    private fun shutdownStorage() {
        val storage = StorageFactory.instance
        storage!!.shutdown()
    }

    private fun scheduleTasks() {
        SimpleMarketService.getInstance()!!.scheduleSellingTask()
        SimpleMarketService.getInstance()!!.scheduleConfirmTokenTask()
    }

    private fun registerCommands() {
        getCommand("market")!!.setExecutor(MarketCommandManager(this))
    }

    private fun setupEconomy(): Boolean {
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
            get() = LocalizationManager.getInstance(
                getPlugin(
                    FwAuctionHouse::class.java
                )
            )
    }
}