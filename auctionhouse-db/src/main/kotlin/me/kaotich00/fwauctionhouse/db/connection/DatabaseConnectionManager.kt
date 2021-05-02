package me.kaotich00.fwauctionhouse.db.connection

import me.kaotich00.fwauctionhouse.db.connection.util.DatabaseCredentials

interface DatabaseConnectionManager {

    fun init(databaseCredentials: DatabaseCredentials)

    fun shutdown()
}