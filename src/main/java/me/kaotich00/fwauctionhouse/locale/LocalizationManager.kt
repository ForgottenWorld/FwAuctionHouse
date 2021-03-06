package me.kaotich00.fwauctionhouse.locale

import me.kaotich00.fwauctionhouse.FwAuctionHouse
import org.bukkit.ChatColor
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.plugin.java.JavaPlugin
import java.io.File
import java.util.*

object LocalizationManager {

    private val plugin by lazy {
        JavaPlugin.getPlugin(FwAuctionHouse::class.java)
    }
    private const val DEFAULT_LANG_FILE = "language_en_EN.yml"
    private val strings = mutableMapOf<String, String>()

    private fun initDefaultLocalization() {
        plugin.saveResource(DEFAULT_LANG_FILE, true)
    }

    fun loadLanguageFile() {
        initDefaultLocalization()
        val localizationFile = FwAuctionHouse.defaultConfig.getString("localization.language_file") ?: DEFAULT_LANG_FILE

        val data = YamlConfiguration.loadConfiguration(File(plugin.dataFolder, localizationFile))
        for (key in data.getConfigurationSection("strings")!!.getKeys(false)) {
            strings[key] = data.getString("strings.$key")!!
        }
    }

    fun reload() {
        strings.clear()
        loadLanguageFile()
    }

    fun localize(key: String) = strings[key] ?: "${ChatColor.RED}No translation present for $key"

}