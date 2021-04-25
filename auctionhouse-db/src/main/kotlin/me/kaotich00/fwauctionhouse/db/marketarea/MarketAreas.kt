package me.kaotich00.fwauctionhouse.db.marketarea

import org.jetbrains.exposed.dao.id.IntIdTable

object MarketAreas : IntIdTable("market_areas") {

    val worldUUID = varchar("world_uuid", 36)

    val minX = integer("min_x")

    val maxX = integer("max_x")

    val minZ = integer("min_z")

    val maxZ = integer("max_z")

    val chunkKeyList = varchar("chunk_keys", 255)
}