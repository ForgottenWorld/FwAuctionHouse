package me.kaotich00.fwauctionhouse.ktor

import java.io.File

internal object ApiGuard {

    private lateinit var apiKey: String

    fun initialize() {
        if (this::apiKey.isInitialized) error("API key already initialized")
        val file = File("api_key")
        if (file.exists()) {
            apiKey = file.readText()
            return
        }
        file.writeText(DEFAULT_API_KEY)
        apiKey = DEFAULT_API_KEY
    }

    fun validateRequest(apiKey: String) = this.apiKey == apiKey


    private const val DEFAULT_API_KEY = "12345678901234567890123456789012"
}