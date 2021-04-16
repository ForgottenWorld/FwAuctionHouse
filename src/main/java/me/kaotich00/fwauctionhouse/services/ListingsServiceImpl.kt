package me.kaotich00.fwauctionhouse.services

import com.google.inject.Inject
import com.google.inject.Singleton
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import me.kaotich00.fwauctionhouse.FwAuctionHouse
import me.kaotich00.fwauctionhouse.message.Message
import me.kaotich00.fwauctionhouse.model.listing.Listing
import me.kaotich00.fwauctionhouse.storage.ListingsDao
import me.kaotich00.fwauctionhouse.utils.BukkitDispatchers
import me.kaotich00.fwauctionhouse.model.listing.ListingStatus
import me.kaotich00.fwauctionhouse.model.session.PlayerSession
import me.kaotich00.fwauctionhouse.utils.launch
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.event.ClickEvent
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.Bukkit

@Singleton
class ListingsServiceImpl @Inject constructor(
    private val listingsDao: ListingsDao
) : ListingsService {

    private val listings = mutableMapOf<Int, Listing>()

    private val playerSessions = mutableMapOf<Int, PlayerSession>()


    override fun scheduleSellingTask() {
        launch {
            while(true) {
                delay(1000)

                val listings = withContext(BukkitDispatchers.async) {
                    listingsDao.getListings()
                }

                for (listing in listings) {
                    val buyerName = listing.buyerName ?: continue

                    val player = Bukkit.getPlayer(buyerName)

                    if (player == null) {
                        val offlinePlayer = Bukkit.getOfflinePlayerIfCached(buyerName)
                        if (offlinePlayer == null) {
                            listingsDao.updateListingStatus(
                                listing.id.value,
                                ListingStatus.NO_USER_FOUND
                            )
                        }
                        continue
                    }

                    if (getListing(listing.id.value) != null) {
                        continue
                    }

                    if (player.inventory.firstEmpty() == -1) {
                        Message.INVENTORY_FULL.send(player)
                        continue
                    }

                    if (FwAuctionHouse.economy.getBalance(player) < listing.total) {
                        Message.NOT_ENOUGH_MONEY.send(player)
                        listingsDao.updateListingStatus(
                            listing.id.value,
                            ListingStatus.NOT_ENOUGH_MONEY
                        )
                        continue
                    }

                    this@ListingsServiceImpl.listings[listing.id.value] = listing

                    val confirmPurchase = Component.text("[CLICK HERE TO CONFIRM]")
                        .color(NamedTextColor.GREEN)
                        .clickEvent(ClickEvent.runCommand("/market confirm ${listing.id.value}"))
                        .hoverEvent(
                            Component.text("Click to accept the purchase")
                                .color(NamedTextColor.GREEN)
                                .decorate(TextDecoration.ITALIC)
                                .asHoverEvent()
                        )

                    val declinePurchase = Component.text("[CLICK HERE TO DECLINE]\n")
                        .color(NamedTextColor.RED)
                        .clickEvent(ClickEvent.runCommand("/market decline ${listing.id.value}"))
                        .hoverEvent(
                            Component.text("Click to decline the purchase")
                                .color(NamedTextColor.RED)
                                .decorate(TextDecoration.ITALIC)
                                .asHoverEvent()
                        )

                    val itemStack = listing.itemStack

                    val message = Message.PURCHASE_MESSAGE
                        .asComponent(itemStack.i18NDisplayName ?: "N/D", itemStack.amount)
                        .append(confirmPurchase)
                        .append(Component.text(" "))
                        .append(declinePurchase)
                        .append(
                            Component.text("------------------------------------------")
                                .color(NamedTextColor.GREEN)
                                .decorate(TextDecoration.STRIKETHROUGH)
                                .decorate(TextDecoration.BOLD)
                        )

                    player.sendMessage(message)
                }
            }
        }
    }

    override fun scheduleConfirmTokenTask() {
        launch {
            while(true) {
                delay(2000)

                val playerSessions = withContext(BukkitDispatchers.async) {
                    listingsDao.getPlayerSessions()
                }

                for (playerSession in playerSessions) {
                    val player = Bukkit.getPlayer(playerSession.username) ?: continue

                    if (getPlayerSession(playerSession.id.value) != null) continue

                    this@ListingsServiceImpl.playerSessions[playerSession.id.value] = playerSession

                    val confirmPurchase =
                        Component.text("[CLICK HERE TO CONFIRM YOUR IDENTITY]\n", NamedTextColor.GREEN)
                            .clickEvent(
                                ClickEvent.runCommand("/market validateToken ${playerSession.id.value}")
                            ).hoverEvent(
                                Component.text("Click to validate your identity", NamedTextColor.GREEN)
                                    .decorate(TextDecoration.ITALIC)
                                    .asHoverEvent()
                            )

                    val message = Message.VALIDATED_TOKEN_MESSAGE
                        .asComponent()
                        .append(confirmPurchase)
                        .append(
                            Component.text("------------------------------------------")
                                .color(NamedTextColor.GREEN)
                                .decorate(TextDecoration.STRIKETHROUGH)
                                .decorate(TextDecoration.BOLD)
                        )

                    player.sendMessage(message)
                }
            }
        }
    }

    override fun removeFromListings(listing: Listing) {
        listings.remove(listing.id.value)
    }

    override fun getListing(id: Int) = listings[id]

    override fun removeFromPlayerSession(playerSession: PlayerSession) {
        playerSessions.remove(playerSession.id.value)
    }

    override fun getPlayerSession(id: Int) = playerSessions[id]
}