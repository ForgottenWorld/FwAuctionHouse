package me.kaotich00.fwauctionhouse.storage

import me.kaotich00.fwauctionhouse.db.listing.Listing
import me.kaotich00.fwauctionhouse.db.listing.Listings
import me.kaotich00.fwauctionhouse.db.session.PlayerSession
import me.kaotich00.fwauctionhouse.db.session.PlayerSessions
import me.kaotich00.fwauctionhouse.utils.Base64ItemStackConverter.toBase64
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
                this.itemStackBase64 = itemStack.toBase64()
                additionalData = itemStack.i18NDisplayName
                minecraftEnum = itemStack.type.toString()
                itemName = itemStack.i18NDisplayName!!
            }
        }
    }

    override fun getListings() = transaction {
        Listing.find { Listings.status eq 2 }.toList()
    }

    override fun updateListingStatus(listingId: Int, status: Listing.Status) {
        transaction {
            Listing.findById(listingId)?.status = status.value
        }
    }

    override fun deleteListing(listingId: Int) {
        transaction {
            Listing.findById(listingId)?.delete()
        }
    }

    override fun getPlayerSessions() = transaction {
        PlayerSession
            .find { PlayerSessions.isValidated neq true }
            .toList()
    }

    override fun validateToken(sessionId: Int) {
        transaction {
            PlayerSession.findById(sessionId)?.isValidated = true
        }
    }
}