package me.kaotich00.fwauctionhouse;

import me.kaotich00.fwauctionhouse.commands.MarketCommandManager;
import me.kaotich00.fwauctionhouse.locale.LocalizationManager;
import me.kaotich00.fwauctionhouse.services.SimpleMarketService;
import me.kaotich00.fwauctionhouse.storage.Storage;
import me.kaotich00.fwauctionhouse.storage.StorageFactory;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public final class FwAuctionHouse extends JavaPlugin {

    public static FileConfiguration defaultConfig;
    public static Economy economyService;

    @Override
    public void onEnable() {
        ConsoleCommandSender sender = Bukkit.getConsoleSender();

        sender.sendMessage(ChatColor.DARK_GRAY + "" + ChatColor.STRIKETHROUGH + "=====================[" + ChatColor.GRAY + " Fw" + ChatColor.GREEN + "Market " + ChatColor.DARK_GRAY + "]======================");

        sender.sendMessage(ChatColor.GRAY + "   >> " + ChatColor.RESET + " Loading configuration...");
        loadConfiguration();

        sender.sendMessage(ChatColor.GRAY + "   >> " + ChatColor.RESET + " Initializing database...");
        initStorage();

        sender.sendMessage(ChatColor.GRAY + "   >> " + ChatColor.RESET + " Loading localization...");
        LocalizationManager.getInstance(this).loadLanguageFile();

        sender.sendMessage(ChatColor.GRAY + "   >> " + ChatColor.RESET + " Registering commands...");
        registerCommands();

        sender.sendMessage(ChatColor.GRAY + "   >> " + ChatColor.RESET + " Scheduling tasks...");
        scheduleTasks();

        sender.sendMessage(ChatColor.GRAY + "   >> " + ChatColor.RESET + " Registering economy...");
        if (!setupEconomy()) {
            this.getLogger().severe("This plugin needs Vault and an Economy plugin in order to function!");
            Bukkit.getPluginManager().disablePlugin(this);
        }

        sender.sendMessage(ChatColor.DARK_GRAY + "" + ChatColor.STRIKETHROUGH +  "====================================================");
    }

    @Override
    public void onDisable() {
        shutdownStorage();
    }

    private void loadConfiguration() {
        getConfig().options().copyDefaults(true);
        saveDefaultConfig();
        defaultConfig = getConfig();
    }

    public static FileConfiguration getDefaultConfig() {
        return defaultConfig;
    }

    public void reloadDefaultConfig() {
        reloadConfig();
        defaultConfig = getConfig();
    }

    public void initStorage() {
        Storage storage = StorageFactory.getInstance();
        storage.init();
    }

    public void shutdownStorage() {
        Storage storage = StorageFactory.getInstance();
        storage.shutdown();
    }

    public void scheduleTasks() {
        SimpleMarketService.getInstance().scheduleSellingTask();
        SimpleMarketService.getInstance().scheduleConfirmTokenTask();
    }

    public void registerCommands() {
        getCommand("market").setExecutor(new MarketCommandManager(this));
    }

    public boolean setupEconomy() {
        if (Bukkit.getPluginManager().getPlugin("Vault") == null) {
            return false;
        }

        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        economyService = rsp.getProvider();
        return economyService != null;
    }

    public static Economy getEconomy() {
        return economyService;
    }

    public static LocalizationManager getLocalizationManager() {
        return LocalizationManager.getInstance(FwAuctionHouse.getPlugin(FwAuctionHouse.class));
    }

}
