package me.kaotich00.fwauctionhouse.services

import com.palmergames.bukkit.towny.TownyAPI
import me.kaotich00.fwauctionhouse.model.MarketArea
import org.bukkit.entity.Player

class MarketAreaServiceImpl : MarketAreaService {

    private val marketAreasById = mutableMapOf<Int, MarketArea>()

    private val marketAreasByChunkKey = mutableMapOf<Long, List<MarketArea>>()

    private val marketAreasByTown = mutableMapOf<String, List<MarketArea>>()


    override fun addMarketArea(marketArea: MarketArea) {
        marketAreasById[marketArea.id] = marketArea

        for (chunkKey in marketArea.chunkKeys) {
            val current = marketAreasByChunkKey[chunkKey]
            marketAreasByChunkKey[chunkKey] = current?.plus(marketArea) ?: listOf(marketArea)
        }

        if (marketArea.townName != null) {
            val current = marketAreasByTown[marketArea.townName]
            marketAreasByTown[marketArea.townName] = current?.plus(marketArea) ?: listOf(marketArea)
        }
    }

    override fun removeMarketArea(marketArea: MarketArea) {
        marketAreasById.remove(marketArea.id)

        for (chunkKey in marketArea.chunkKeys) {
            marketAreasByChunkKey[chunkKey]?.let {
                marketAreasByChunkKey[chunkKey] = it - marketArea
            }
        }

        if (marketArea.townName != null) {
            marketAreasByTown[marketArea.townName]?.let {
                marketAreasByTown[marketArea.townName] = it - marketArea
            }
        }
    }

    override fun getMarketAreaById(id: Int) = marketAreasById[id]

    override fun getMarketAreaPlayerIsIn(player: Player): MarketArea? {
        val bucket = marketAreasByChunkKey[player.chunk.chunkKey] ?: return null
        val location = player.location
        val x = location.blockX
        val z = location.blockZ
        return bucket.find { it.containsXZ(x,z) }
    }

    override fun canPlayerUseMarketArea(player: Player, marketArea: MarketArea): Boolean {
        if (marketArea.townName == null) {
            return player.hasPermission("market.international")
        }

        if (!player.hasPermission("market.town")) return false

        return TownyAPI
            .getInstance()
            .getTown(marketArea.townName)
            ?.hasResident(player.name) == true
    }
}