package me.kaotich00.fwauctionhouse.locale

import me.kaotich00.fwauctionhouse.FwAuctionHouse
import org.bukkit.ChatColor
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.configuration.file.YamlConfiguration
import java.io.File
import java.util.*

class LocalizationManager private constructor(plugin: FwAuctionHouse) {
    private val plugin: FwAuctionHouse
    private val defaultLanguageFile = "language_en_EN.yml"
    private val strings: MutableMap<String?, String>
    fun initDefaultLocalization() {
        plugin.saveResource(defaultLanguageFile, true)
    }

    fun loadLanguageFile() {
        val defaultConfig: FileConfiguration? = FwAuctionHouse.defaultConfig
        initDefaultLocalization()
        var localizationFile = defaultConfig?.getString("localization.language_file")
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