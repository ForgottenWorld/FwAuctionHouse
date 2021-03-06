package me.kaotich00.fwauctionhouse.objects

import org.bukkit.inventory.ItemStack

data class PendingSell(val listingId: Int, var itemStack: ItemStack, var buyerName: String, var totalCost: Double)