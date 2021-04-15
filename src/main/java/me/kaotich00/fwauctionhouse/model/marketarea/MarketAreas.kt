package me.kaotich00.fwauctionhouse.model.marketarea

import org.jetbrains.exposed.dao.id.IntIdTable

object MarketAreas : IntIdTable("market_areas") {

    val worldUUID = varchar("world_uuid", 36)

    val minX = integer("min_x")

    val maxX = integer("min_x")

    val minZ = integer("min_x")

    val maxZ = integer("min_x")

    val chunkKeyList = varchar("chunk_keys", 255)
}