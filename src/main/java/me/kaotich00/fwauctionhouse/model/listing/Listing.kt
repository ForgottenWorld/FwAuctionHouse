package me.kaotich00.fwauctionhouse.model.listing

import me.kaotich00.fwauctionhouse.utils.Base64ItemStackConverter
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID

class Listing(id: EntityID<Int>): IntEntity(id) {

    companion object : IntEntityClass<Listing>(Listings)

    
    var sellerUuid by Listings.sellerUuid

    var sellerNickname by Listings.sellerNickname

    var amount by Listings.amount

    var unitPrice by Listings.unitPrice

    var status by Listings.status

    var buyerName by Listings.buyerName

    var itemStackBase64 by Listings.itemStack

    var additionalData by Listings.additionalData

    var minecraftEnum by Listings.minecraftEnum

    var itemName by Listings.itemName


    val total get() = amount * unitPrice

    val itemStack get() = Base64ItemStackConverter.fromBase64(itemStackBase64)
}