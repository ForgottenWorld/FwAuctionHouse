package me.kaotich00.fwauctionhouse.storage.sql

import me.kaotich00.fwauctionhouse.FwAuctionHouse
import java.sql.Connection

interface ConnectionFactory {

    fun init(plugin: FwAuctionHouse)

    fun shutdown()

    val connection: Connection
}