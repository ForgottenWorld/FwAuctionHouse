package me.kaotich00.fwauctionhouse.services

interface WebApiService {

    fun setWebApiHost(apiHost: String)

    fun setWebApiKey(apiKey: String)

    fun setWebApiPort(port: Int)

    fun sendOnListingsChangedEvent()

    fun sendOnTokenConfirmedEvent(token: String)
}