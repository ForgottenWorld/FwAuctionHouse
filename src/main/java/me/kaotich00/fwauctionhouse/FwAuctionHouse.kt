package me.kaotich00.fwauctionhouse

import me.kaotich00.fwauctionhouse.commands.MarketCommandManager
import me.kaotich00.fwauctionhouse.locale.LocalizationManager
import me.kaotich00.fwauctionhouse.services.SimpleMarketService
import me.kaotich00.fwauctionhouse.storage.StorageProvider
import net.milkbowl.vault.economy.Economy
import org.bukkit.Bukkit
import org.bukkit.ChatColor.*
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.plugin.java.JavaPlugin

class FwAuctionHouse : JavaPlugin() {

    override fun onEnable() {
        val sender = Bukkit.getConsoleSender()

        sender.sendMessage("$DARK_GRAY$STRIKETHROUGH=====================[$GRAY Fw${GREEN}Market $DARK_GRAY]======================")

        sender.sendMessage("$GRAY   >> $RESET Loading configuration...")
        loadConfiguration()

        sender.sendMessage("$GRAY   >> $RESET Initializing database...")
        initStorage()

        sender.sendMessage("$GRAY   >> $RESET Loading localization...")
        LocalizationManager.loadLanguageFile()

        sender.sendMessage("$GRAY   >> $RESET Registering commands...")
        registerCommands()

        sender.sendMessage("$GRAY   >> $RESET Scheduling tasks...")
        scheduleTasks()

        // sender.sendMessage("$GRAY   >> $RESET Registering economy...")

        sender.sendMessage("$DARK_GRAY$STRIKETHROUGH====================================================")
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

        val instance get() = getPlugin(FwAuctionHouse::class.java)

        val economy by lazy {
            Bukkit.getServer()
                .servicesManager
                .getRegistration(Economy::class.java)
                ?.provider
                ?: error("Economy service not present")
        }
    }
}