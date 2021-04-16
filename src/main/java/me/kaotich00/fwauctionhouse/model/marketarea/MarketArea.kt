package me.kaotich00.fwauctionhouse.model.marketarea

import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.World
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.*
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

class MarketArea(id: EntityID<Int>): IntEntity(id) {

    companion object : IntEntityClass<MarketArea>(MarketAreas) {

        fun builder() = Builder()
    }


    var minX by MarketAreas.minX

    var maxX by MarketAreas.maxX

    var minZ by MarketAreas.minZ

    var maxZ by MarketAreas.maxZ


    private var _worldUUID by MarketAreas.worldUUID

    var worldUUID: UUID
        get() = UUID.fromString(_worldUUID)
        set(value) {
            _worldUUID = value.toString()
        }


    private var _chunkKeys by MarketAreas.chunkKeyList

    var chunkKeys: Set<Long>
        get() = _chunkKeys.split(",").map { it.toLong() }.toSet()
        set(value) {
            _chunkKeys = value.joinToString(",")
        }


    fun containsLocation(location: Location) = worldUUID == location.world?.uid &&
            location.blockX in minX..maxX &&
            location.blockZ in minZ..maxZ


    class Builder internal constructor() {

        private var pos1: Pair<Int, Int>? = null

        private var pos2: Pair<Int, Int>? = null

        private var worldUUID: UUID? = null

        private var chunkKeys: Set<Long>? = null

        fun pos1(pos1: Location): Builder {
            this.pos1 = pos1.blockX to pos1.blockZ
            worldUUID = pos1.world.uid
            chunkKeys(pos1.world)
            return this
        }

        fun pos2(pos2: Location): Builder {
            this.pos2 = pos2.blockX to pos2.blockZ
            worldUUID = pos2.world.uid
            chunkKeys(pos2.world)
            return this
        }

        private fun chunkKeys(world: World) {
            val pos1 = pos1 ?: return
            val pos2 = pos2 ?: return

            val minX = min(pos1.first, pos2.first)
            val maxX = max(pos1.first, pos2.first)
            val minZ = min(pos1.second, pos2.second)
            val maxZ = max(pos1.second, pos2.second)

            val keys = mutableSetOf<Long>()
            for (x in Math.floorMod(minX, 16)..Math.floorMod(maxX, 16) step 16) {
                for (z in Math.floorMod(minZ, 16)..Math.floorMod(maxZ, 16) step 16) {
                    keys.add(world.getChunkAt(x,z).chunkKey)
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
}