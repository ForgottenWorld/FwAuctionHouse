package me.kaotich00.fwauctionhouse.storage.hikari

import com.zaxxer.hikari.HikariConfig

class MySQLConnectionProvider : HikariConnectionProvider() {

    override val drivers = "com.mysql.jdbc.jdbc2.optional.MysqlDataSource"

    override fun addConnectionProperties(
        config: HikariConfig,
        properties: MutableMap<String, String>
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