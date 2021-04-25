package me.kaotich00.fwauctionhouse.model

import me.kaotich00.fwauctionhouse.db.listing.Listing
import me.kaotich00.fwauctionhouse.db.marketarea.MarketArea
import me.kaotich00.fwauctionhouse.utils.Base64ItemStackConverter
import org.bukkit.Location

fun MarketArea.containsLocation(location: Location) = worldUUID == location.world?.uid &&
        location.blockX in minX..maxX &&
        location.blockZ in minZ..maxZ

val Listing.total get() = amount * unitPrice

val Listing.itemStack get() = Base64ItemStackConverter.fromBase64(itemStackBase64)