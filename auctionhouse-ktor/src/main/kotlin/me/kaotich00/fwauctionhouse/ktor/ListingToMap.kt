package me.kaotich00.fwauctionhouse.ktor

import me.kaotich00.fwauctionhouse.db.listing.Listing

fun Listing.toMap() = mapOf(
    "seller_uuid" to sellerUuid,
    "seller_nickname" to sellerNickname,
    "amount" to amount,
    "unit_price" to unitPrice,
    "status" to status,
    "buyer_name" to buyerName,
    "additional_data" to additionalData,
    "minecraft_enum" to minecraftEnum,
    "item_name" to itemName
)