package me.kaotich00.fwauctionhouse.ktor

internal object ApiGuard {

    private lateinit var apiKey: String

    fun initialize(config: Configuration) {
        if (this::apiKey.isInitialized) error("API key already initialized")
        apiKey = config.apiKey
    }

    fun validateRequest(apiKey: String) = this.apiKey == apiKey
}