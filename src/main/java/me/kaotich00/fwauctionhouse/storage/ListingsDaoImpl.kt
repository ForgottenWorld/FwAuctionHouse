package me.kaotich00.fwauctionhouse.storage

import me.kaotich00.fwauctionhouse.model.listing.Listing
import me.kaotich00.fwauctionhouse.model.listing.ListingStatus
import me.kaotich00.fwauctionhouse.model.listing.Listings
import me.kaotich00.fwauctionhouse.model.session.PlayerSession
import me.kaotich00.fwauctionhouse.model.session.PlayerSessions
import me.kaotich00.fwauctionhouse.utils.Base64ItemStackConverter
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.jetbrains.exposed.sql.transactions.transaction

class ListingsDaoImpl : ListingsDao {

    override fun insertListing(
        seller: Player,
        itemStack: ItemStack,
        unitPrice: Double
    ) {
        transaction {
            Listing.new {
                sellerUuid = seller.uniqueId.toString()
                sellerNickname = seller.name
                amount = itemStack.amount
                this.unitPrice = unitPrice.toFloat()
                status = 1
                buyerName = null
                this.itemStackBase64 = Base64ItemStackConverter.toBase64(itemStack)
                additionalData = itemStack.i18NDisplayName
                minecraftEnum = itemStack.type.toString()
                itemName = itemStack.i18NDisplayName!!
            }
        }
    }

    override fun getListings() = Listing.find { Listings.status eq 2 }.toList()

    override fun updateListingStatus(listingId: Int, status: ListingStatus) {
        transaction {
            Listing.findById(listingId)?.status = status.value
        }
    }

    override fun deleteListing(listingId: Int) {
        transaction {
            Listing.findById(listingId)?.delete()
        }
    }

    override fun getPlayerSessions() = PlayerSession
        .find { PlayerSessions.isValidated neq true }
        .toList()

    override fun validateToken(sessionId: Int) {
        transaction {
            PlayerSession.findById(sessionId)?.isValidated = true
        }
    }
}