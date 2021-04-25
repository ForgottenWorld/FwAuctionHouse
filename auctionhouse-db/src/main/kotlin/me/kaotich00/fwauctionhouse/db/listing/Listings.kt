package me.kaotich00.fwauctionhouse.db.listing

import org.jetbrains.exposed.dao.id.IntIdTable

object Listings : IntIdTable("listing") {

    val sellerUuid = varchar("seller_uuid", 36)

    val sellerNickname = varchar("seller_nickname", 30)

    val amount = integer("amount")

    val unitPrice = float("unit_price")

    val status = integer("status")

    val buyerName = varchar("buyer_name", 30).nullable()

    val itemStack = text("item_stack")

    val additionalData = varchar("additional_data", 255).nullable()

    val minecraftEnum = varchar("minecraft_enum", 255)

    val itemName = varchar("item_name", 255)
}