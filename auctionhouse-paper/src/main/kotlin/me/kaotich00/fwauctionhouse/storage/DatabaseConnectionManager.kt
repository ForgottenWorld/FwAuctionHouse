package me.kaotich00.fwauctionhouse.storage

import me.kaotich00.fwauctionhouse.FwAuctionHouse
import me.kaotich00.fwauctionhouse.storage.util.DatabaseCredentials

interface DatabaseConnectionManager {

    fun init(plugin: FwAuctionHouse, databaseCredentials: DatabaseCredentials)

    fun shutdown()
}