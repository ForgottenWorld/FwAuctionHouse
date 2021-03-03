package me.kaotich00.fwauctionhouse.storage.sql

import me.kaotich00.fwauctionhouse.FwAuctionHouse
import java.sql.Connection
import java.sql.SQLException

interface ConnectionFactory {
    fun init(plugin: FwAuctionHouse?)

    @Throws(Exception::class)
    fun shutdown()

    @get:Throws(SQLException::class)
    val connection: Connection
}