package me.kaotich00.fwauctionhouse.storage.sql.hikari

import com.zaxxer.hikari.HikariConfig
import me.kaotich00.fwauctionhouse.storage.util.StorageCredentials

class MySQLConnectionFactory(credentials: StorageCredentials) : HikariConnectionFactory(credentials) {

    override val drivers: String
        get() = "com.mysql.jdbc.jdbc2.optional.MysqlDataSource"

    override fun addConnectionProperties(
        config: HikariConfig,
        properties: MutableMap<String?, String?>
    ) {
        with(properties) {
            putIfAbsent("cachePrepStmts", "true")
            putIfAbsent("prepStmtCacheSize", "250")
            putIfAbsent("prepStmtCacheSqlLimit", "2048")
            putIfAbsent("useServerPrepStmts", "true")
            putIfAbsent("useLocalSessionState", "true")
            putIfAbsent("rewriteBatchedStatements", "true")
            putIfAbsent("cacheResultSetMetadata", "true")
            putIfAbsent("cacheServerConfiguration", "true")
            putIfAbsent("elideSetAutoCommits", "true")
            putIfAbsent("maintainTimeStats", "false")
            putIfAbsent("alwaysSendSetIsolation", "false")
            putIfAbsent("cacheCallableStmts", "true")
        }
        super.addConnectionProperties(config, properties)
    }

}