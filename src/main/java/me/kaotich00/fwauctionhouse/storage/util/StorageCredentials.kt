package me.kaotich00.fwauctionhouse.storage.util

import java.sql.Connection
import java.sql.DriverManager
import java.sql.SQLException

class StorageCredentials(
    val host: String,
    val database: String,
    val username: String,
    val password: String
) {

    val maxPoolSize = 10

    val minIdleConnections = 10

    val maxLifetime = 1800000

    val connectionTimeout = 5000

    fun toConnection(): Connection = DriverManager.getConnection(
        "jdbc:mysql://$host/$database?useSSL=false",
        username,
        password
    )
}