package me.kaotich00.fwauctionhouse.model

import me.kaotich00.fwauctionhouse.db.marketarea.MarketArea
import me.kaotich00.fwauctionhouse.utils.alignToGrid
import org.bukkit.Chunk
import org.bukkit.Location
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.*
import kotlin.math.max
import kotlin.math.min

class MarketAreaBuilder internal constructor() {

    private var pos1: Pair<Int, Int>? = null

    private var pos2: Pair<Int, Int>? = null

    private var worldUUID: UUID? = null

    private var chunkKeys: Set<Long>? = null


    fun pos1(pos1: Location): MarketAreaBuilder {
        this.pos1 = pos1.blockX to pos1.blockZ
        worldUUID = pos1.world.uid
        chunkKeys()
        return this
    }

    fun pos2(pos2: Location): MarketAreaBuilder {
        this.pos2 = pos2.blockX to pos2.blockZ
        worldUUID = pos2.world.uid
        chunkKeys()
        return this
    }

    private fun chunkKeys() {
        val pos1 = pos1 ?: return
        val pos2 = pos2 ?: return

        val minX = min(pos1.first, pos2.first).alignToGrid()
        val maxX = max(pos1.first, pos2.first).alignToGrid()
        val minZ = min(pos1.second, pos2.second).alignToGrid()
        val maxZ = max(pos1.second, pos2.second).alignToGrid()

        val keys = mutableSetOf<Long>()
        for (x in minX..maxX step 16) {
            for (z in minZ..maxZ step 16) {
                keys.add(Chunk.getChunkKey(x, z))
            }
        }
        chunkKeys = keys
    }

    fun canBuild() = pos1 != null &&
        pos2 != null &&
        worldUUID != null &&
        chunkKeys != null

    fun build(): MarketArea {
        val pos1 = pos1!!
        val pos2 = pos2!!
        val worldUUID = worldUUID!!
        val chunkKeys = chunkKeys!!

        val minX = min(pos1.first, pos2.first)
        val maxX = max(pos1.first, pos2.first)
        val minZ = min(pos1.second, pos2.second)
        val maxZ = max(pos1.second, pos2.second)

        return transaction {
            MarketArea.new {
                this.minX = minX
                this.maxX = maxX
                this.minZ = minZ
                this.maxZ = maxZ
                this.worldUUID = worldUUID
                this.chunkKeys = chunkKeys
            }
        }
    }
}