package me.kaotich00.fwauctionhouse.storage.sql

import me.kaotich00.fwauctionhouse.FwAuctionHouse
import me.kaotich00.fwauctionhouse.objects.PendingSell
import me.kaotich00.fwauctionhouse.objects.PendingToken
import me.kaotich00.fwauctionhouse.storage.StorageMethod
import me.kaotich00.fwauctionhouse.utils.SerializationUtil
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import java.io.IOException
import java.sql.Connection
import java.sql.SQLException
import java.util.*

class SqlStorage(override val plugin: FwAuctionHouse, private val connectionFactory: ConnectionFactory) :
    StorageMethod {
    override fun init() {
        connectionFactory.init(plugin)
    }

    override fun shutdown() {
        try {
            connectionFactory.shutdown()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @get:Throws(SQLException::class)
    override val connection: Connection?
        get() = connectionFactory.connection

    override fun insertListing(seller: Player, itemStack: ItemStack, unitPrice: Float?) {
        try {
            connection.use { c ->
                val insertListing = c!!.prepareStatement(INSERT_LISTING)
                insertListing.setString(1, seller.uniqueId.toString())
                insertListing.setString(2, seller.name)
                insertListing.setInt(3, itemStack.amount)
                insertListing.setFloat(4, unitPrice!!)
                insertListing.setString(5, SerializationUtil.toBase64(itemStack))
                insertListing.setString(6, itemStack.i18NDisplayName)
                insertListing.setString(7, itemStack.type.toString())
                insertListing.setString(8, itemStack.i18NDisplayName)
                insertListing.executeUpdate()
            }
        } catch (e: SQLException) {
            e.printStackTrace()
        }
    }

    override val pendingSells: List<PendingSell>
        get() {
            val pendingSellList: MutableList<PendingSell> = ArrayList()
            try {
                connection.use { c ->
                    c!!.prepareStatement(SELECT_PENDING_SELLS).use { ps ->
                        try {
                            ps.executeQuery().use { rs ->
                                while (rs.next()) {
                                    val listingId = rs.getInt("id")
                                    val buyerName = rs.getString("buyer_name")
                                    val amount = rs.getInt("amount")
                                    val totalCost = rs.getFloat("unit_price") * amount
                                    val itemType = rs.getString("item_stack")
                                    val itemStack = SerializationUtil.fromBase64(itemType)
                                    if (buyerName != null && itemStack != null) {
                                        val pendingSell = PendingSell(listingId, itemStack, buyerName, totalCost)
                                        pendingSellList.add(pendingSell)
                                    }
                                }
                            }
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    }
                }
            } catch (e: SQLException) {
                e.printStackTrace()
            }
            return pendingSellList
        }

    override fun updateListingStatus(listingId: Int, status: Int) {
        try {
            connection.use { c ->
                val updateListing = c!!.prepareStatement(UPDATE_LISTING_STATUS)
                updateListing.setInt(1, status)
                updateListing.setInt(2, listingId)
                updateListing.executeUpdate()
            }
        } catch (e: SQLException) {
            e.printStackTrace()
        }
    }

    override fun deletePendingSell(listingId: Int) {
        try {
            connection.use { c ->
                val updateListing = c!!.prepareStatement(DELETE_PENDING_SELLS)
                updateListing.setInt(1, listingId)
                updateListing.executeUpdate()
            }
        } catch (e: SQLException) {
            e.printStackTrace()
        }
    }

    override val pendingTokens: List<PendingToken>
        get() {
            val pendingTokens: MutableList<PendingToken> = ArrayList()
            try {
                connection.use { c ->
                    c!!.prepareStatement(SELECT_PENDING_TOKENS).use { ps ->
                        ps.executeQuery().use { rs ->
                            while (rs.next()) {
                                val sessionId = rs.getInt("id")
                                val username = rs.getString("username")
                                val token = rs.getString("token")
                                val pendingToken = PendingToken(sessionId, username, token)
                                pendingTokens.add(pendingToken)
                            }
                        }
                    }
                }
            } catch (e: SQLException) {
                e.printStackTrace()
            }
            return pendingTokens
        }

    override fun validateToken(sessionId: Int) {
        try {
            connection.use { c ->
                val updateListing = c!!.prepareStatement(VALIDATE_TOKEN)
                updateListing.setInt(1, sessionId)
                updateListing.executeUpdate()
            }
        } catch (e: SQLException) {
            e.printStackTrace()
        }
    }

    companion object {
        private const val INSERT_LISTING =
            "INSERT INTO listing(`seller_uuid`,`seller_nickname`,`amount`,`unit_price`,`status`,`item_stack`,`additional_data`,`minecraft_enum`,`item_name`) VALUES (?,?,?,?,1,?,?,?,?)"
        private const val SELECT_PENDING_SELLS = "SELECT * FROM listing WHERE status = 2"
        private const val UPDATE_LISTING_STATUS = "UPDATE listing SET status = ? WHERE id = ?"
        private const val DELETE_PENDING_SELLS = "DELETE FROM listing WHERE id = ?"
        private const val SELECT_PENDING_TOKENS =
            "SELECT * FROM player_session WHERE token IS NOT NULL AND (is_validated IS NULL OR is_validated <> 1)"
        private const val VALIDATE_TOKEN = "UPDATE player_session SET is_validated = 1 WHERE id = ?"
    }
}