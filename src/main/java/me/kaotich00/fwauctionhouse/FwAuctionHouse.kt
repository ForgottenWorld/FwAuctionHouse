package me.kaotich00.fwauctionhouse

import com.google.inject.Inject
import me.kaotich00.fwauctionhouse.commands.MarketCommandManager
import me.kaotich00.fwauctionhouse.locale.LocalizationManager
import me.kaotich00.fwauctionhouse.message.Message
import me.kaotich00.fwauctionhouse.services.ListingsService
import me.kaotich00.fwauctionhouse.services.MarketAreaService
import me.kaotich00.fwauctionhouse.storage.DatabaseConnectionManager
import me.kaotich00.fwauctionhouse.storage.util.DatabaseCredentials
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.TextComponent
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration
import net.milkbowl.vault.economy.Economy
import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin

class FwAuctionHouse : JavaPlugin() {

    @Inject
    private lateinit var listingsService: ListingsService

    @Inject
    private lateinit var marketCommandManager: MarketCommandManager

    @Inject
    private lateinit var databaseConnectionManager: DatabaseConnectionManager

    @Inject
    private lateinit var localizationManager: LocalizationManager

    @Inject
    private lateinit var bukkitEventListener: BukkitEventListener

    @Inject
    private lateinit var marketAreaService: MarketAreaService


    override fun onEnable() {
        val sender = Bukkit.getConsoleSender()

        sender.sendMessage(
            TextComponent.ofChildren(
                Component.text("=====================[ ", NamedTextColor.DARK_GRAY),
                Component.text("Fw", NamedTextColor.GRAY),
                Component.text("Market ", NamedTextColor.GREEN),
                Component.text("]======================", NamedTextColor.GRAY)
            ).decorate(TextDecoration.STRIKETHROUGH)
        )

        val prefix = Component.text("   >>  ", NamedTextColor.GRAY)

        sender.sendMessage(prefix.append(Component.text("Loading configuration...")))
        loadConfiguration()

        sender.sendMessage(prefix.append(Component.text("Injecting dependencies...")))
        try {
            DependenciesModule(this)
                .createInjector()
                .injectMembers(this)
        } catch (e: Exception) {
            e.printStackTrace()
            throw e
        }

        sender.sendMessage(prefix.append(Component.text("Initializing database...")))
        initStorage()

        sender.sendMessage(prefix.append(Component.text("Loading localization...")))
        Message.localizationManager = localizationManager
        localizationManager.loadLanguageFile(this)

        sender.sendMessage(prefix.append(Component.text("Registering commands...")))
        registerCommands()

        sender.sendMessage(prefix.append(Component.text("Registering event listeners...")))
        Bukkit.getPluginManager().registerEvents(bukkitEventListener, this)

        sender.sendMessage(prefix.append(Component.text("Scheduling tasks...")))
        scheduleTasks()

        // sender.sendMessage("$GRAY   >> $RESET Registering economy...")

        sender.sendMessage(
            Component
                .text("=".repeat(52), NamedTextColor.DARK_GRAY)
                .decorate(TextDecoration.STRIKETHROUGH)
        )

        marketAreaService.initialize()
    }

    override fun onDisable() {
        Message.localizationManager = null
        shutdownStorage()
    }

    private fun loadConfiguration() {
        config.options().copyDefaults(true)
        saveDefaultConfig()
    }

    fun reloadConfiguration() {
        localizationManager.reload(this)
    }

    private fun initStorage() {
        val credentials = with(config) {
            DatabaseCredentials(
                host = getString("address")!!,
                database = getString("database")!!,
                username = getString("username")!!,
                password = getString("password")!!
            )
        }
        databaseConnectionManager.init(this, credentials)
    }

    private fun shutdownStorage() {
        databaseConnectionManager.shutdown()
    }

    private fun scheduleTasks() {
        listingsService.scheduleSellingTask()
        listingsService.scheduleConfirmTokenTask()
    }

    private fun registerCommands() {
        getCommand("market")!!.setExecutor(marketCommandManager)
    }


    companion object {

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