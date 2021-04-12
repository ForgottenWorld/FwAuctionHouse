package me.kaotich00.fwauctionhouse.storage

import me.kaotich00.fwauctionhouse.FwAuctionHouse
import me.kaotich00.fwauctionhouse.storage.sql.SqlStorage
import me.kaotich00.fwauctionhouse.storage.sql.hikari.MySQLConnectionFactory
import me.kaotich00.fwauctionhouse.storage.util.StorageCredentials
import org.bukkit.plugin.java.JavaPlugin

object StorageProvider {

    val storageInstance by lazy {
        Storage(FwAuctionHouse.instance, getStorageFromConfig())
    }

    private fun getStorageFromConfig(): StorageMethod {
        with(FwAuctionHouse.defaultConfig) {
            val host = getString("address")!!
            val database = getString("database")!!
            val username = getString("username")!!
            val password = getString("password")!!
            val credentials = StorageCredentials(host, database, username, password)

            return SqlStorage(FwAuctionHouse.instance, MySQLConnectionFactory(credentials))
        }
    }
}