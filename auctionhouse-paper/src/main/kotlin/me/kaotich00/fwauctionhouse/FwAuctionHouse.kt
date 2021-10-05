package me.kaotich00.fwauctionhouse

import com.google.inject.Inject
import me.kaotich00.fwauctionhouse.commands.MarketCommandManager
import me.kaotich00.fwauctionhouse.locale.LocalizationManager
import me.kaotich00.fwauctionhouse.message.Message
import me.kaotich00.fwauctionhouse.services.ListingsService
import me.kaotich00.fwauctionhouse.services.MarketAreaService
import me.kaotich00.fwauctionhouse.services.WebApiService
import me.kaotich00.fwauctionhouse.db.connection.DatabaseConnectionManager
import me.kaotich00.fwauctionhouse.db.connection.util.DatabaseCredentials
import me.kaotich00.fwauctionhouse.integration.TownyIntegrationManager
import me.kaotich00.fwauctionhouse.message.send
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.JoinConfiguration
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

    @Inject
    private lateinit var webApiService: WebApiService

    @Inject
    private lateinit var townyIntegrationManager: TownyIntegrationManager

    override fun onEnable() {
        val sender = Bukkit.getConsoleSender()

        Component.join(
            JoinConfiguration.noSeparators(),
            Component.text("=====================[ ", NamedTextColor.DARK_GRAY),
            Component.text("Fw", NamedTextColor.GRAY),
            Component.text("Market ", NamedTextColor.GREEN),
            Component.text("]======================", NamedTextColor.GRAY)
        ).decorate(TextDecoration.STRIKETHROUGH).send(sender)

        val prefix = Component.text("   >>  ", NamedTextColor.GRAY)

        prefix.append(Component.text("Loading configuration...")).send(sender)
        loadConfiguration()

        prefix.append(Component.text("Injecting dependencies...")).send(sender)
        try {
            DependenciesModule(this)
                .createInjector()
                .injectMembers(this)
        } catch (e: Exception) {
            e.printStackTrace()
            throw e
        }

        prefix.append(Component.text("Loading localization...")).send(sender)
        Message.localizationManager = localizationManager
        localizationManager.loadLanguageFile(this)

        prefix.append(Component.text("Initializing database...")).send(sender)
        initStorage()

        prefix.append(Component.text("Initializing Web API service...")).send(sender)
        initWebApiService()

        prefix.append(Component.text("Registering commands...")).send(sender)
        registerCommands()

        prefix.append(Component.text("Registering event listeners...")).send(sender)
        Bukkit.getPluginManager().registerEvents(bukkitEventListener, this)

        prefix.append(Component.text("Checking for Towny integration...")).send(sender)
        townyIntegrationManager.checkIntegration()

        prefix.append(Component.text("Scheduling tasks...")).send(sender)
        scheduleTasks()

        Component
            .text("=".repeat(52), NamedTextColor.DARK_GRAY)
            .decorate(TextDecoration.STRIKETHROUGH)
            .send(sender)

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

    private fun initWebApiService() {
        with(config) {
            webApiService.setWebApiHost(getString("webApiHost")!!)
            webApiService.setWebApiKey(getString("webApiKey")!!)
            webApiService.setWebApiPort(getInt("webApiPort"))
        }
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
        databaseConnectionManager.init(credentials)
    }

    private fun shutdownStorage() {
        databaseConnectionManager.shutdown()
    }

    private fun scheduleTasks() {
        listingsService.scheduleSellingTask()
        listingsService.scheduleTokenConfirmationTask()
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