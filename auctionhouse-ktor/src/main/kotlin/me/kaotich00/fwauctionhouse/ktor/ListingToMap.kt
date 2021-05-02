package me.kaotich00.fwauctionhouse.ktor

import me.kaotich00.fwauctionhouse.db.listing.Listing

fun Listing.toMap() = mapOf(
    "id" to id.value.toString(),
    "sellerUuid" to sellerUuid,
    "sellerNickname" to sellerNickname,
    "amount" to amount.toString(),
    "unitPrice" to unitPrice.toString(),
    "status" to status.toString(),
    "buyerName" to (buyerName ?: ""),
    "additionalData" to (additionalData ?: ""),
    "minecraftEnum" to minecraftEnum,
    "itemName" to itemName
)