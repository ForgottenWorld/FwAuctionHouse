package me.kaotich00.fwauctionhouse.locale

import me.kaotich00.fwauctionhouse.FwAuctionHouse
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.configuration.file.YamlConfiguration
import java.io.File
import javax.inject.Singleton

@Singleton
class LocalizationManager {

    private val strings = mutableMapOf<String, String>()


    fun loadLanguageFile(plugin: FwAuctionHouse) {
        plugin.saveResource(DEFAULT_LANG_FILE, true)
        val localizationFile = plugin.config
            .getString("localization.language_file")
            ?: DEFAULT_LANG_FILE

        val data = YamlConfiguration.loadConfiguration(File(plugin.dataFolder, localizationFile))
        for (key in data.getKeys(false)) {
            strings[key] = data.getString(key)!!
        }
    }

    fun reload(plugin: FwAuctionHouse) {
        strings.clear()
        loadLanguageFile(plugin)
    }

    fun localize(key: String, params: Array<out Any>) = strings[key]
        ?.let { Component.text(it.format(*params)) }
        ?: Component.text("No translation present for $key", NamedTextColor.RED)


    companion object {

        private const val DEFAULT_LANG_FILE = "language_en_EN.yml"
    }
}