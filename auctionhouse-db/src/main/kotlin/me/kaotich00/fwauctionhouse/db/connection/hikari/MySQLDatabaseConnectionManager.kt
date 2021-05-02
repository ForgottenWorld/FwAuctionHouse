package me.kaotich00.fwauctionhouse.db.connection.hikari


class MySQLDatabaseConnectionManager : HikariDatabaseConnectionManager() {

    override val drivers = "com.mysql.cj.jdbc.MysqlDataSource"

    override fun getConnectionProperties() = mapOf(
        "cachePrepStmts" to "true",
        "prepStmtCacheSize" to "250",
        "prepStmtCacheSqlLimit" to "2048",
        "useServerPrepStmts" to "true",
        "useLocalSessionState" to "true",
        "rewriteBatchedStatements" to "true",
        "cacheResultSetMetadata" to "true",
        "cacheServerConfiguration" to "true",
        "elideSetAutoCommits" to "true",
        "maintainTimeStats" to "false",
        "alwaysSendSetIsolation" to "false",
        "cacheCallableStmts" to "true"
    )
}