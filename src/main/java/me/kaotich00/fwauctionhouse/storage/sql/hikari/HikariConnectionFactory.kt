package me.kaotich00.fwauctionhouse.storage.sql.hikari

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import me.kaotich00.fwauctionhouse.FwAuctionHouse
import me.kaotich00.fwauctionhouse.storage.sql.ConnectionFactory
import me.kaotich00.fwauctionhouse.storage.util.StorageCredentials
import java.sql.Connection
import java.sql.SQLException

open class HikariConnectionFactory(protected val configuration: StorageCredentials) : ConnectionFactory {
    private var hikari: HikariDataSource? = null
    protected open val drivers: String?
        protected get() = null

    protected open fun addConnectionProperties(config: HikariConfig, properties: MutableMap<String?, String?>) {
        for ((key, value) in properties) {
            config.addDataSourceProperty(key, value)
        }
    }

    protected fun addConnectionInfo(config: HikariConfig?) {
        var address = configuration.host
        val addressSplit = address!!.split(":").toTypedArray()
        address = addressSplit[0]
        val port = if (addressSplit.size > 1) addressSplit[1] else "3306"
        config!!.dataSourceClassName = drivers
        config.addDataSourceProperty("serverName", address)
        config.addDataSourceProperty("port", port)
        config.addDataSourceProperty("databaseName", configuration.database)
        config.addDataSourceProperty("useSSL", false)
        config.username = configuration.username
        config.password = configuration.password
    }

    override fun init(plugin: FwAuctionHouse?) {
        var config: HikariConfig? = null
        try {
            config = HikariConfig()
        } catch (e: LinkageError) {
            e.printStackTrace()
        }
        config!!.poolName = "fwauctionhouse-hikari"
        addConnectionInfo(config)
        config.initializationFailTimeout = -1
        hikari = HikariDataSource(config)
    }

    override fun shutdown() {
        if (hikari != null) {
            hikari!!.close()
        }
    }

    @get:Throws(SQLException::class)
    override val connection: Connection
        get() {
            if (hikari == null) {
                throw SQLException("Unable to get a connection from the pool. (hikari is null)")
            }
            return hikari!!.connection
                ?: throw SQLException("Unable to get a connection from the pool. (getConnection returned null)")
        }
}