package me.kaotich00.fwauctionhouse.storage.sql.hikari

import com.zaxxer.hikari.HikariConfig
import me.kaotich00.fwauctionhouse.storage.util.StorageCredentials

class MySQLConnectionFactory(credentials: StorageCredentials) : HikariConnectionFactory(credentials) {
    override val drivers: String?
        protected get() = "com.mysql.jdbc.jdbc2.optional.MysqlDataSource"

    override fun addConnectionProperties(config: HikariConfig, properties: MutableMap<String?, String?>) {
        properties.putIfAbsent("cachePrepStmts", "true")
        properties.putIfAbsent("prepStmtCacheSize", "250")
        properties.putIfAbsent("prepStmtCacheSqlLimit", "2048")
        properties.putIfAbsent("useServerPrepStmts", "true")
        properties.putIfAbsent("useLocalSessionState", "true")
        properties.putIfAbsent("rewriteBatchedStatements", "true")
        properties.putIfAbsent("cacheResultSetMetadata", "true")
        properties.putIfAbsent("cacheServerConfiguration", "true")
        properties.putIfAbsent("elideSetAutoCommits", "true")
        properties.putIfAbsent("maintainTimeStats", "false")
        properties.putIfAbsent("alwaysSendSetIsolation", "false")
        properties.putIfAbsent("cacheCallableStmts", "true")
        super.addConnectionProperties(config, properties)
    }
}