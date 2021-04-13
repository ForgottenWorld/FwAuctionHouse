package me.kaotich00.fwauctionhouse.services

import com.google.inject.Inject
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import me.kaotich00.fwauctionhouse.FwAuctionHouse
import me.kaotich00.fwauctionhouse.message.Message
import me.kaotich00.fwauctionhouse.model.PendingSell
import me.kaotich00.fwauctionhouse.model.PendingToken
import me.kaotich00.fwauctionhouse.storage.ListingsDao
import me.kaotich00.fwauctionhouse.utils.BukkitDispatchers
import me.kaotich00.fwauctionhouse.model.ListingStatus
import me.kaotich00.fwauctionhouse.utils.launch
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.event.ClickEvent
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.Bukkit

class ListingsServiceImpl @Inject constructor(
    private val listingsDao: ListingsDao
) : ListingsService {

    private val pendingSells = mutableMapOf<Int, PendingSell>()

    private val pendingTokens = mutableMapOf<Int, PendingToken>()


    override fun scheduleSellingTask() {
        launch {
            while(true) {
                delay(1000)

                val pendingSells = withContext(BukkitDispatchers.async) {
                    listingsDao.getPendingSells()
                }

                for (pendingSell in pendingSells) {
                    val player = Bukkit.getPlayer(pendingSell.buyerName)

                    if (player == null) {
                        val offlinePlayer = Bukkit.getOfflinePlayerIfCached(pendingSell.buyerName)
                        if (offlinePlayer == null) {
                            listingsDao.updateListingStatus(
                                pendingSell.listingId,
                                ListingStatus.NO_USER_FOUND
                            )
                        }
                        continue
                    }

                    if (getPendingSell(pendingSell.listingId) != null) {
                        continue
                    }

                    if (player.inventory.firstEmpty() == -1) {
                        Message.INVENTORY_FULL.send(player)
                        continue
                    }

                    if (FwAuctionHouse.economy.getBalance(player) < pendingSell.totalCost) {
                        Message.NOT_ENOUGH_MONEY.send(player)
                        listingsDao.updateListingStatus(
                            pendingSell.listingId,
                            ListingStatus.NOT_ENOUGH_MONEY
                        )
                        continue
                    }

                    this@ListingsServiceImpl.pendingSells[pendingSell.listingId] = pendingSell

                    val confirmPurchase = Component.text("[CLICK HERE TO CONFIRM]")
                        .color(NamedTextColor.GREEN)
                        .clickEvent(ClickEvent.runCommand("/market confirm ${pendingSell.listingId}"))
                        .hoverEvent(
                            Component
                                .text("Click to accept the purchase")
                                .color(NamedTextColor.GREEN)
                                .decorate(TextDecoration.ITALIC)
                                .asHoverEvent()
                        )

                    val declinePurchase = Component.text("[CLICK HERE TO DECLINE]\n")
                        .color(NamedTextColor.RED)
                        .clickEvent(ClickEvent.runCommand("/market decline ${pendingSell.listingId}"))
                        .hoverEvent(
                            Component.text("Click to decline the purchase")
                                .color(NamedTextColor.RED)
                                .decorate(TextDecoration.ITALIC)
                                .asHoverEvent()
                        )

                    val message = Message.PURCHASE_MESSAGE.asComponent(
                        pendingSell.itemStack.i18NDisplayName ?: "N/D",
                        pendingSell.itemStack.amount
                    ).append(confirmPurchase)
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

                val pendingTokens = withContext(BukkitDispatchers.async) {
                    listingsDao.getPendingTokens()
                }

                for (pendingToken in pendingTokens) {
                    val player = Bukkit.getPlayer(pendingToken.username) ?: continue

                    if (getPendingToken(pendingToken.sessionId) != null) {
                        continue
                    }

                    this@ListingsServiceImpl.pendingTokens[pendingToken.sessionId] = pendingToken

                    val confirmPurchase =
                        Component.text("[CLICK HERE TO CONFIRM YOUR IDENTITY]\n", NamedTextColor.GREEN)
                            .clickEvent(ClickEvent.runCommand("/market validateToken ${pendingToken.sessionId}"))
                            .hoverEvent(
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

    override fun removeFromPendingSells(pendingSell: PendingSell) {
        pendingSells.remove(pendingSell.listingId)
    }

    override fun getPendingSell(id: Int) = pendingSells[id]

    override fun removeFromPendingToken(pendingToken: PendingToken) {
        pendingTokens.remove(pendingToken.sessionId)
    }

    override fun getPendingToken(id: Int) = pendingTokens[id]
}