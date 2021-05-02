package me.kaotich00.fwauctionhouse.ktor

import kotlinx.serialization.Serializable

@Serializable
data class Configuration(
    val apiAddress: String,
    val apiPort: Int,
    val apiKey: String,
    val dbAddress: String,
    val dbPort: Int,
    val dbName: String,
    val dbUsername: String,
    val dbPassword: String
) {

    companion object {

        val DEFAULT get() = Configuration(
            apiAddress = "127.0.0.1",
            apiPort = 8080,
            apiKey = "12345678901234567890123456789012",
            dbAddress = "localhost",
            dbPort = 3306,
            dbName = "database",
            dbUsername = "username",
            dbPassword = "password"
        )
    }
}