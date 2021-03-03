package me.kaotich00.fwauctionhouse.storage

import me.kaotich00.fwauctionhouse.FwAuctionHouse

class Storage(private val plugin: FwAuctionHouse, val storageMethod: StorageMethod) {
    fun init() {
        try {
            storageMethod.init()
        } catch (e: Exception) {
            plugin.logger.severe("Failed to init storage implementation")
            e.printStackTrace()
        }
    }

    fun shutdown() {
        try {
            storageMethod.shutdown()
        } catch (e: Exception) {
            plugin.logger.severe("Failed to shutdown storage implementation")
            e.printStackTrace()
        }
    }
}