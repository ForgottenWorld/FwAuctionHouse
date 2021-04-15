package me.kaotich00.fwauctionhouse.services

import me.kaotich00.fwauctionhouse.model.marketarea.MarketArea
import org.bukkit.entity.Player

interface MarketAreaService {

    fun initialize()

    fun addMarketArea(marketArea: MarketArea)

    fun removeMarketArea(marketArea: MarketArea)

    fun getMarketAreaById(id: Int): MarketArea?

    fun canPlayerUseMarket(player: Player): Boolean

    fun getMarketAreaPlayerIsIn(player: Player): MarketArea?


    fun enterBuildMode(player: Player)

    fun exitBuildMode(player: Player, disconnect: Boolean)

    fun buildPos1(player: Player)

    fun buildPos2(player: Player)
}