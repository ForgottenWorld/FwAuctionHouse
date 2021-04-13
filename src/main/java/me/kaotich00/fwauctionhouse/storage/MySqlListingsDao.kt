package me.kaotich00.fwauctionhouse.storage

import me.kaotich00.fwauctionhouse.model.PendingSell
import me.kaotich00.fwauctionhouse.model.PendingToken
import me.kaotich00.fwauctionhouse.model.ListingStatus
import me.kaotich00.fwauctionhouse.utils.Base64ItemStackConverter
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import java.io.IOException
import java.sql.Connection
import java.sql.SQLException

class MySqlListingsDao(private val connectionProvider: ConnectionProvider) : ListingsDao {

    private val connection: Connection
        get() = connectionProvider.connection

    override fun insertListing(
        seller: Player,
        itemStack: ItemStack,
        unitPrice: Double
    ) {
        try {
            connection.use { c ->
                with (c.prepareStatement(Queries.INSERT_LISTING)) {
                    setString(1, seller.uniqueId.toString())
                    setString(2, seller.name)
                    setInt(3, itemStack.amount)
                    setDouble(4, unitPrice)
                    setString(5, Base64ItemStackConverter.toBase64(itemStack))
                    setString(6, itemStack.i18NDisplayName)
                    setString(7, itemStack.type.toString())
                    setString(8, itemStack.i18NDisplayName)
                    executeUpdate()
                }
            }
        } catch (e: SQLException) {
            e.printStackTrace()
        }
    }

    override fun getPendingSells(): List<PendingSell> {
        val pendingSellList = mutableListOf<PendingSell>()
        try {
            connection.use { c ->
                c.prepareStatement(Queries.SELECT_PENDING_SELLS).use { ps ->
                    ps.executeQuery().use { rs ->
                        while (rs.next()) {
                            val listingId = rs.getInt("id")
                            val buyerName = rs.getString("buyer_name")
                            val amount = rs.getInt("amount")
                            val totalCost = rs.getDouble("unit_price") * amount
                            val itemType = rs.getString("item_stack")
                            val itemStack = itemType?.let(Base64ItemStackConverter::fromBase64)
                            if (buyerName == null || itemStack == null) continue
                            pendingSellList.add(PendingSell(listingId, itemStack, buyerName, totalCost))
                        }
                    }
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return pendingSellList
    }

    override fun updateListingStatus(listingId: Int, status: ListingStatus) {
        try {
            connection.use { c ->
                val updateListing = c.prepareStatement(Queries.UPDATE_LISTING_STATUS)
                updateListing.setInt(1, status.value)
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
                val updateListing = c.prepareStatement(Queries.DELETE_PENDING_SELLS)
                updateListing.setInt(1, listingId)
                updateListing.executeUpdate()
            }
        } catch (e: SQLException) {
            e.printStackTrace()
        }
    }

    override fun getPendingTokens(): List<PendingToken> {
        val pendingTokens = mutableListOf<PendingToken>()
        try {
            connection.use { c ->
                c.prepareStatement(Queries.SELECT_PENDING_TOKENS).use { ps ->
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
                val updateListing = c.prepareStatement(Queries.VALIDATE_TOKEN)
                updateListing.setInt(1, sessionId)
                updateListing.executeUpdate()
            }
        } catch (e: SQLException) {
            e.printStackTrace()
        }
    }

    private object Queries {

        const val INSERT_LISTING = "INSERT INTO listing(`seller_uuid`," +
                "`seller_nickname`,`amount`,`unit_price`," +
                "`status`,`item_stack`,`additional_data`," +
                "`minecraft_enum`,`item_name`) VALUES (?,?,?,?,1,?,?,?,?)"

        const val SELECT_PENDING_SELLS = "SELECT * FROM listing WHERE status = 2"

        const val UPDATE_LISTING_STATUS = "UPDATE listing SET status = ? WHERE id = ?"

        const val DELETE_PENDING_SELLS = "DELETE FROM listing WHERE id = ?"

        const val SELECT_PENDING_TOKENS = "SELECT * FROM player_session " +
                "WHERE token IS NOT NULL AND (is_validated IS NULL OR is_validated <> 1)"

        const val VALIDATE_TOKEN = "UPDATE player_session SET is_validated = 1 WHERE id = ?"
    }
}