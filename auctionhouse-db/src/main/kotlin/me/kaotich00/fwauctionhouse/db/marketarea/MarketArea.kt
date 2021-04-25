package me.kaotich00.fwauctionhouse.db.marketarea

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import java.util.*

class MarketArea(id: EntityID<Int>): IntEntity(id) {

    companion object : IntEntityClass<MarketArea>(MarketAreas)


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
}