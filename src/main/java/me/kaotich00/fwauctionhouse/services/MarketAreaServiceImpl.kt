package me.kaotich00.fwauctionhouse.services

import com.palmergames.bukkit.towny.TownyAPI
import com.palmergames.bukkit.towny.`object`.WorldCoord
import me.kaotich00.fwauctionhouse.message.Message
import me.kaotich00.fwauctionhouse.model.marketarea.MarketArea
import org.bukkit.entity.Player
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.*
import javax.inject.Singleton

@Singleton
class MarketAreaServiceImpl : MarketAreaService {

    private val marketAreasById = mutableMapOf<Int, MarketArea>()

    private val marketAreasByChunkKey = mutableMapOf<Long, List<MarketArea>>()


    private val playerBuilders = mutableMapOf<UUID, MarketArea.Builder>()


    override fun initialize() {
        transaction {
            MarketArea.all().forEach { addMarketArea(it) }
        }
    }

    override fun addMarketArea(marketArea: MarketArea) {
        marketAreasById[marketArea.id.value] = marketArea

        for (chunkKey in marketArea.chunkKeys) {
            val current = marketAreasByChunkKey[chunkKey]
            marketAreasByChunkKey[chunkKey] = current?.plus(marketArea) ?: listOf(marketArea)
        }
    }

    override fun removeMarketArea(marketArea: MarketArea) {
        marketAreasById.remove(marketArea.id.value)

        for (chunkKey in marketArea.chunkKeys) {
            marketAreasByChunkKey[chunkKey]?.let {
                marketAreasByChunkKey[chunkKey] = it - marketArea
            }
        }
    }

    override fun getMarketAreaById(id: Int) = marketAreasById[id]

    override fun canPlayerUseMarket(player: Player): Boolean {
        if (player.hasPermission("market.international") &&
            getMarketAreaPlayerIsIn(player) != null
        ) return true

        if (!player.hasPermission("market.town")) return false

        val resident = TownyAPI.getInstance()
            .getResident(player.uniqueId)
            ?: return false

        if (!resident.hasTown()) return false
        val worldCoord = WorldCoord.parseWorldCoord(player)

        return resident.town.hasTownBlock(worldCoord)
    }

    override fun getMarketAreaPlayerIsIn(player: Player): MarketArea? {
        val bucket = marketAreasByChunkKey[player.chunk.chunkKey] ?: return null
        val location = player.location
        return bucket.find { it.containsLocation(location) }
    }

    override fun enterBuildMode(player: Player) {
        if (playerBuilders.containsKey(player.uniqueId)) {
            Message.ALREADY_IN_BUILD_MODE.send(player)
            return
        }

        playerBuilders[player.uniqueId] = MarketArea.builder()
        Message.ENTER_BUILD_MODE.send(player)
    }

    override fun exitBuildMode(player: Player, disconnect: Boolean) {
        if (playerBuilders.remove(player.uniqueId) == null) {
            Message.NOT_IN_BUILD_MODE.send(player)
            return
        }

        if (disconnect) return
        Message.EXIT_BUILD_MODE.send(player)
    }

    private fun buildPosN(player: Player, posNumber: Int) {
        val builder = playerBuilders[player.uniqueId] ?: run {
            Message.NOT_IN_BUILD_MODE.send(player)
            return
        }

        val block = player.getTargetBlockExact(5) ?: return
        if (!block.isSolid) return
        val location = block.location

        if (posNumber == 1) {
            builder.pos1(location)
            Message.FIRST_POSITION_SET.send(player)
        } else {
            builder.pos2(location)
            Message.SECOND_POSITION_SET.send(player)
        }

        if (!builder.canBuild()) return

        val marketArea = builder.build()
        addMarketArea(marketArea)
        Message.MARKET_AREA_CREATED.send(player)
        exitBuildMode(player, false)
    }

    override fun buildPos1(player: Player) {
        buildPosN(player, 1)
    }

    override fun buildPos2(player: Player) {
        buildPosN(player, 2)
    }
}