package me.kaotich00.fwauctionhouse.storage

import me.kaotich00.fwauctionhouse.FwAuctionHouse
import me.kaotich00.fwauctionhouse.storage.util.StorageCredentials

interface DatabaseConnectionManager {

    fun init(plugin: FwAuctionHouse, storageCredentials: StorageCredentials)

    fun shutdown()
}