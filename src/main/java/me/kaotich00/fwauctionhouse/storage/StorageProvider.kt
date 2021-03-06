package me.kaotich00.fwauctionhouse.storage

import me.kaotich00.fwauctionhouse.FwAuctionHouse
import me.kaotich00.fwauctionhouse.storage.sql.SqlStorage
import me.kaotich00.fwauctionhouse.storage.sql.hikari.MySQLConnectionFactory
import me.kaotich00.fwauctionhouse.storage.util.StorageCredentials
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.plugin.java.JavaPlugin

object StorageProvider {

    val storageInstance by lazy {
        Storage(
            JavaPlugin.getPlugin(
                FwAuctionHouse::class.java
            ), storageFromConfig
        )
    }

    private val storageFromConfig: StorageMethod
        get() {
            with(FwAuctionHouse.defaultConfig) {
                val host        = getString("address")!!
                val database    = getString("database")!!
                val username    = getString("username")!!
                val password    = getString("password")!!
                val credentials = StorageCredentials(host, database, username, password)

                return SqlStorage(JavaPlugin.getPlugin(FwAuctionHouse::class.java), MySQLConnectionFactory(credentials))
            }
        }
}