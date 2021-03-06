package me.kaotich00.fwauctionhouse.storage.sql

import me.kaotich00.fwauctionhouse.FwAuctionHouse
import java.sql.Connection
import java.sql.SQLException

interface ConnectionFactory {
    fun init(plugin: FwAuctionHouse?)
    fun shutdown()
    val connection: Connection
}