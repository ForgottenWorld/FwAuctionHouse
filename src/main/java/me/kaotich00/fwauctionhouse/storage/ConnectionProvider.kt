package me.kaotich00.fwauctionhouse.storage

import me.kaotich00.fwauctionhouse.FwAuctionHouse
import me.kaotich00.fwauctionhouse.storage.util.StorageCredentials
import java.sql.Connection

interface ConnectionProvider {

    fun init(plugin: FwAuctionHouse, storageCredentials: StorageCredentials)

    fun shutdown()

    val connection: Connection
}