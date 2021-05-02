package me.kaotich00.fwauctionhouse.db.connection.hikari

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import me.kaotich00.fwauctionhouse.db.connection.DatabaseConnectionManager
import me.kaotich00.fwauctionhouse.db.connection.util.DatabaseCredentials
import me.kaotich00.fwauctionhouse.db.listing.Listings
import me.kaotich00.fwauctionhouse.db.marketarea.MarketAreas
import me.kaotich00.fwauctionhouse.db.session.PlayerSessions
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.StdOutSqlLogger
import org.jetbrains.exposed.sql.addLogger
import org.jetbrains.exposed.sql.transactions.transaction

abstract class HikariDatabaseConnectionManager : DatabaseConnectionManager {

    private var hikariDataSource: HikariDataSource? = null


    abstract val drivers: String

    protected open fun getConnectionProperties(): Map<String, String> = mapOf()


    private fun createConfiguration(databaseCredentials: DatabaseCredentials): HikariConfig {
        val addressSplit = databaseCredentials.host.split(":")
        val address = addressSplit[0]
        val port = if (addressSplit.size > 1) addressSplit[1] else "3306"

        return HikariConfig().apply {
            poolName = "fwauctionhouse-hikari"
            dataSourceClassName = drivers

            dataSourceProperties.putAll(getConnectionProperties())
            dataSourceProperties["serverName"] = address
            dataSourceProperties["port"] = port
            dataSourceProperties["databaseName"] = databaseCredentials.database
            dataSourceProperties["useSSL"] = false

            username = databaseCredentials.username
            password = databaseCredentials.password

            initializationFailTimeout = -1
        }
    }

    override fun init(databaseCredentials: DatabaseCredentials) {
        val dataSource = HikariDataSource(createConfiguration(databaseCredentials))
        hikariDataSource = dataSource
        Database.connect(dataSource)
        transaction {
            addLogger(StdOutSqlLogger)
            SchemaUtils.create(Listings, MarketAreas, PlayerSessions)
        }
    }

    override fun shutdown() {
        hikariDataSource?.close()
    }
}