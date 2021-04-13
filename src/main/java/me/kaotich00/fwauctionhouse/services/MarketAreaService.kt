package me.kaotich00.fwauctionhouse.services

import me.kaotich00.fwauctionhouse.model.MarketArea
import org.bukkit.entity.Player

interface MarketAreaService {

    fun addMarketArea(marketArea: MarketArea)

    fun removeMarketArea(marketArea: MarketArea)

    fun getMarketAreaById(id: Int): MarketArea?

    fun getMarketAreaPlayerIsIn(player: Player): MarketArea?

    fun canPlayerUseMarketArea(player: Player, marketArea: MarketArea): Boolean
}