package me.kaotich00.fwauctionhouse.model

import java.util.*

data class MarketArea(
    val id: Int,
    val worldUUID: UUID,
    val minX: Int,
    val maxX: Int,
    val minZ: Int,
    val maxZ: Int,
    val chunkKeys: List<Long>,
    val townName: String?
) {

    fun containsXZ(x: Int, z: Int) = x in minX until maxX && z in minZ until maxZ
}