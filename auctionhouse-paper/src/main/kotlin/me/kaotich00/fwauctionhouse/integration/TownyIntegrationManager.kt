package me.kaotich00.fwauctionhouse.integration

import org.bukkit.Bukkit
import javax.inject.Singleton

@Singleton
class TownyIntegrationManager {

    var useTowny = false
        private set

    private fun isTownyPresent() = Bukkit.getPluginManager().getPlugin("Towny") != null

    fun checkIntegration() {
        if (isTownyPresent()) useTowny = true
    }
}