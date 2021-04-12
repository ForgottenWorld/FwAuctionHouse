package me.kaotich00.fwauctionhouse.services

import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import me.kaotich00.fwauctionhouse.FwAuctionHouse
import me.kaotich00.fwauctionhouse.message.Message
import me.kaotich00.fwauctionhouse.objects.PendingSell
import me.kaotich00.fwauctionhouse.objects.PendingToken
import me.kaotich00.fwauctionhouse.storage.StorageProvider
import me.kaotich00.fwauctionhouse.utils.BukkitDispatchers
import me.kaotich00.fwauctionhouse.utils.ListingStatus
import me.kaotich00.fwauctionhouse.utils.launch
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.event.ClickEvent
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.Bukkit

object SimpleMarketService {

    private val pendingSells = mutableMapOf<Int, PendingSell>()
    private val pendingTokens = mutableMapOf<Int, PendingToken>()

    fun scheduleSellingTask() {
        launch {
            while(true) {
                delay(1000)

                val pendingSells = withContext(BukkitDispatchers.async) {
                    StorageProvider.storageInstance.storageMethod.getPendingSells()
                }

                for (pendingSell in pendingSells) {
                    val player = Bukkit.getPlayer(pendingSell.buyerName)

                    if (player == null) {
                        val offlinePlayer = Bukkit.getOfflinePlayerIfCached(pendingSell.buyerName)
                        if (offlinePlayer == null) {
                            StorageProvider.storageInstance.storageMethod.updateListingStatus(
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
                        StorageProvider.storageInstance.storageMethod.updateListingStatus(
                            pendingSell.listingId,
                            ListingStatus.NOT_ENOUGH_MONEY
                        )
                        continue
                    }

                    addToPendingSells(pendingSell)

                    val confirmPurchase = Component.text("[CLICK HERE TO CONFIRM]")
                        .color(NamedTextColor.GREEN)
                        .clickEvent(ClickEvent.runCommand("/market confirm " + pendingSell.listingId))
                        .hoverEvent(
                            Component
                                .text("Click to accept the purchase")
                                .color(NamedTextColor.GREEN)
                                .decorate(TextDecoration.ITALIC)
                                .asHoverEvent()
                        )

                    val declinePurchase = Component.text("[CLICK HERE TO DECLINE]\n")
                        .color(NamedTextColor.RED)
                        .clickEvent(ClickEvent.runCommand("/market decline " + pendingSell.listingId))
                        .hoverEvent(
                            Component.text("Click to decline the purchase")
                                .color(NamedTextColor.RED)
                                .decorate(TextDecoration.ITALIC)
                                .asHoverEvent()
                        )

                    val message = Component.text(
                        Message.PURCHASE_MESSAGE.asString(
                            pendingSell.itemStack.i18NDisplayName ?: "N/D",
                            pendingSell.itemStack.amount
                        )
                    ).append(confirmPurchase)
                        .append(Component.text(" "))
                        .append(declinePurchase)
                        .append(
                            Component.text("""------------------------------------------""")
                                .color(NamedTextColor.GREEN)
                                .decorate(TextDecoration.STRIKETHROUGH)
                                .decorate(TextDecoration.BOLD)
                        )

                    player.sendMessage(message)
                }
            }
        }
    }

    fun scheduleConfirmTokenTask() {
        launch {
            while(true) {
                delay(2000)

                val pendingTokens = withContext(BukkitDispatchers.async) {
                    StorageProvider.storageInstance.storageMethod.getPendingTokens()
                }

                for (pendingToken in pendingTokens) {
                    val player = Bukkit.getPlayer(pendingToken.username) ?: continue

                    if (getPendingToken(pendingToken.sessionId) != null) {
                        continue
                    }

                    addToPendingToken(pendingToken)

                    val confirmPurchase =
                        Component.text("[CLICK HERE TO CONFIRM YOUR IDENTITY]\n", NamedTextColor.GREEN)
                            .clickEvent(ClickEvent.runCommand("/market validateToken " + pendingToken.sessionId))
                            .hoverEvent(
                                Component.text("Click to validate your identity", NamedTextColor.GREEN)
                                    .decorate(TextDecoration.ITALIC)
                                    .asHoverEvent()
                            )

                    val message = Component.text(Message.VALIDATED_TOKEN_MESSAGE.asString())
                        .append(confirmPurchase)
                        .append(
                            Component.text("""------------------------------------------""")
                                .color(NamedTextColor.GREEN)
                                .decorate(TextDecoration.STRIKETHROUGH)
                                .decorate(TextDecoration.BOLD)
                        )

                    player.sendMessage(message)
                }
            }
        }
    }

    fun addToPendingSells(pendingSell: PendingSell) {
        pendingSells[pendingSell.listingId] = pendingSell
    }

    fun removeFromPendingSells(pendingSell: PendingSell) {
        pendingSells.remove(pendingSell.listingId)
    }

    fun getPendingSell(id: Int) = pendingSells[id]

    fun addToPendingToken(pendingToken: PendingToken) {
        pendingTokens[pendingToken.sessionId] = pendingToken
    }

    fun removeFromPendingToken(pendingToken: PendingToken) {
        pendingTokens.remove(pendingToken.sessionId)
    }

    fun getPendingToken(id: Int) = pendingTokens[id]
}