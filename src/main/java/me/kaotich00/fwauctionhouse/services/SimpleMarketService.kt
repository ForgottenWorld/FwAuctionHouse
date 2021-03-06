package me.kaotich00.fwauctionhouse.services

import me.kaotich00.fwauctionhouse.FwAuctionHouse
import me.kaotich00.fwauctionhouse.message.Message
import me.kaotich00.fwauctionhouse.objects.PendingSell
import me.kaotich00.fwauctionhouse.objects.PendingToken
import me.kaotich00.fwauctionhouse.storage.StorageProvider
import me.kaotich00.fwauctionhouse.utils.ListingStatus
import net.md_5.bungee.api.ChatColor
import net.md_5.bungee.api.chat.ClickEvent
import net.md_5.bungee.api.chat.ComponentBuilder
import net.md_5.bungee.api.chat.HoverEvent
import net.md_5.bungee.api.chat.TextComponent
import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin
import java.util.concurrent.CompletableFuture

object SimpleMarketService {

    private val pendingSells = mutableMapOf<Int, PendingSell>()
    private val pendingTokens = mutableMapOf<Int, PendingToken>()

    fun scheduleSellingTask() {
        Bukkit.getScheduler().scheduleSyncRepeatingTask(
            JavaPlugin.getPlugin(
                FwAuctionHouse::class.java
            ), {
                CompletableFuture.supplyAsync {
                    StorageProvider.storageInstance.storageMethod.pendingSells
                }.thenAccept { pendingSells ->
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

                        val confirmPurchase = TextComponent("[CLICK HERE TO CONFIRM]").apply {
                            color = ChatColor.GREEN
                            clickEvent = ClickEvent(ClickEvent.Action.RUN_COMMAND, "/market confirm " + pendingSell.listingId)
                            hoverEvent = HoverEvent(
                                HoverEvent.Action.SHOW_TEXT, ComponentBuilder("Click to accept the purchase").color(
                                    ChatColor.GREEN
                                ).italic(true).create()
                            )
                        }

                        val declinePurchase = TextComponent("[CLICK HERE TO DECLINE]\n").apply {
                            color = ChatColor.RED
                            clickEvent = ClickEvent(ClickEvent.Action.RUN_COMMAND, "/market decline " + pendingSell.listingId)
                            hoverEvent = HoverEvent(
                                HoverEvent.Action.SHOW_TEXT, ComponentBuilder("Click to decline the purchase").color(
                                    ChatColor.RED
                                ).italic(true).create()
                            )
                        }

                        val message = ComponentBuilder().append(
                                Message.PURCHASE_MESSAGE.asString(
                                    pendingSell.itemStack.i18NDisplayName ?: "N/D",
                                    pendingSell.itemStack.amount
                                )
                            )
                            .append(confirmPurchase)
                            .append(" ")
                            .append(declinePurchase)
                            .append(
                                """
                                ${ChatColor.GREEN}${ChatColor.STRIKETHROUGH}${ChatColor.BOLD}
                                ------------------------------------------
                                """.trimIndent()
                            )
                        player.spigot().sendMessage(*message.create())
                    }
                }
            }, 20, 20
        )
    }

    fun scheduleConfirmTokenTask() {
        Bukkit.getScheduler().scheduleSyncRepeatingTask(
            JavaPlugin.getPlugin(
                FwAuctionHouse::class.java
            ), {
                CompletableFuture.supplyAsync {
                    StorageProvider.storageInstance.storageMethod.pendingTokens
                }.thenAccept { pendingTokens ->
                    for (pendingToken in pendingTokens) {
                        val player = Bukkit.getPlayer(pendingToken.username) ?: continue
                        if (getPendingToken(pendingToken.sessionId) != null) {
                            continue
                        }
                        addToPendingToken(pendingToken)
                        val confirmPurchase = TextComponent("[CLICK HERE TO CONFIRM YOUR IDENTITY]\n").apply {
                            color = ChatColor.GREEN
                            clickEvent =
                                ClickEvent(ClickEvent.Action.RUN_COMMAND, "/market validateToken " + pendingToken.sessionId)
                            hoverEvent = HoverEvent(
                                HoverEvent.Action.SHOW_TEXT, ComponentBuilder("Click to validate your identity").color(
                                    ChatColor.GREEN
                                ).italic(true).create()
                            )
                        }

                        val message = ComponentBuilder()
                            .append(Message.VALIDATED_TOKEN_MESSAGE.asString())
                            .append(confirmPurchase)
                            .append(
                                """
                                            ${ChatColor.GREEN}${ChatColor.STRIKETHROUGH}${ChatColor.BOLD}
                                            ------------------------------------------
                                            """.trimIndent()
                            )
                        player.spigot().sendMessage(*message.create())
                    }
                }
            }, 40, 40
        )
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