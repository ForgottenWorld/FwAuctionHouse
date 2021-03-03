package me.kaotich00.fwauctionhouse.storage

import me.kaotich00.fwauctionhouse.FwAuctionHouse
import me.kaotich00.fwauctionhouse.storage.sql.SqlStorage
import me.kaotich00.fwauctionhouse.storage.sql.hikari.MySQLConnectionFactory
import me.kaotich00.fwauctionhouse.storage.util.StorageCredentials
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.plugin.java.JavaPlugin

object StorageFactory {
    var storage: Storage? = null
    val instance: Storage?
        get() {
            if (storage != null) {
                return storage
            }
            storage = Storage(
                JavaPlugin.getPlugin(
                    FwAuctionHouse::class.java
                ), storageFromConfig
            )
            return storage
        }
    private val storageFromConfig: StorageMethod
        private get() {
            val defaultConfig: FileConfiguration? = FwAuctionHouse.defaultConfig
            val host = defaultConfig?.getString("address")!!
            val database = defaultConfig.getString("database")!!
            val username = defaultConfig.getString("username")!!
            val password = defaultConfig.getString("password")!!
            val credentials = StorageCredentials(host, database, username, password)
            return SqlStorage(JavaPlugin.getPlugin(FwAuctionHouse::class.java), MySQLConnectionFactory(credentials))
        }
}