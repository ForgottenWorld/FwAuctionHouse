package me.kaotich00.fwauctionhouse

import com.google.inject.Inject
import me.kaotich00.fwauctionhouse.services.MarketAreaService
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerQuitEvent

class BukkitEventListener @Inject constructor(
    private val marketAreaService: MarketAreaService
) : Listener {

    @EventHandler
    fun onPlayerQuit(event: PlayerQuitEvent) {
        marketAreaService.exitBuildMode(event.player, true)
    }
}