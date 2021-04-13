package me.kaotich00.fwauctionhouse.storage.hikari

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import me.kaotich00.fwauctionhouse.FwAuctionHouse
import me.kaotich00.fwauctionhouse.storage.ConnectionProvider
import me.kaotich00.fwauctionhouse.storage.util.StorageCredentials
import java.sql.Connection
import java.sql.SQLException

abstract class HikariConnectionProvider : ConnectionProvider {

    private var hikari: HikariDataSource? = null

    abstract val drivers: String

    protected open fun addConnectionProperties(config: HikariConfig, properties: MutableMap<String, String>) {
        for ((key, value) in properties) {
            config.addDataSourceProperty(key, value)
        }
    }

    private fun addConnectionInfo(storageCredentials: StorageCredentials, config: HikariConfig) {
        val addressSplit = storageCredentials.host.split(":")
        val address = addressSplit[0]
        val port = if (addressSplit.size > 1) addressSplit[1] else "3306"

        config.dataSourceClassName = drivers
        config.addDataSourceProperty("serverName", address)
        config.addDataSourceProperty("port", port)
        config.addDataSourceProperty("databaseName", storageCredentials.database)
        config.addDataSourceProperty("useSSL", false)
        config.username = storageCredentials.username
        config.password = storageCredentials.password
    }

    override fun init(plugin: FwAuctionHouse, storageCredentials: StorageCredentials) {
        val config = try {
            HikariConfig()
        } catch (e: LinkageError) {
            e.printStackTrace()
            throw e
        }
        config.poolName = "fwauctionhouse-hikari"
        addConnectionInfo(storageCredentials, config)
        config.initializationFailTimeout = -1
        hikari = HikariDataSource(config)
    }

    override fun shutdown() {
        hikari?.close()
    }

    override val connection: Connection
        get() {
            val hikari = this.hikari
                ?: throw SQLException("Unable to get a connection from the pool. (hikari is null)")

            return hikari.connection
                ?: throw SQLException("Unable to get a connection from the pool. (getConnection returned null)")
        }
}