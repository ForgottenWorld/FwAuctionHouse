package me.kaotich00.fwauctionhouse.storage.hikari

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import me.kaotich00.fwauctionhouse.FwAuctionHouse
import me.kaotich00.fwauctionhouse.storage.DatabaseConnectionManager
import me.kaotich00.fwauctionhouse.storage.util.StorageCredentials
import org.jetbrains.exposed.sql.Database

abstract class HikariDatabaseConnectionManager : DatabaseConnectionManager {

    private var hikariDataSource: HikariDataSource? = null


    abstract val drivers: String

    protected open fun getConnectionProperties(): Map<String, String> = mapOf()


    private fun createConfiguration(storageCredentials: StorageCredentials): HikariConfig {
        val addressSplit = storageCredentials.host.split(":")
        val address = addressSplit[0]
        val port = if (addressSplit.size > 1) addressSplit[1] else "3306"

        return HikariConfig().apply {
            poolName = "fwauctionhouse-hikari"
            dataSourceClassName = drivers

            dataSourceProperties.putAll(getConnectionProperties())
            dataSourceProperties["serverName"] = address
            dataSourceProperties["port"] = port
            dataSourceProperties["databaseName"] = storageCredentials.database
            dataSourceProperties["useSSL"] = false

            username = storageCredentials.username
            password = storageCredentials.password

            initializationFailTimeout = -1
        }
    }

    override fun init(plugin: FwAuctionHouse, storageCredentials: StorageCredentials) {
        hikariDataSource = HikariDataSource(createConfiguration(storageCredentials))
        Database.connect(hikariDataSource!!)
    }

    override fun shutdown() {
        hikariDataSource?.close()
    }
}