package me.kaotich00.fwauctionhouse.services

import java.util.stream.Collectors
import kotlin.Throws
import java.lang.IllegalStateException
import java.io.ByteArrayOutputStream
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder
import java.lang.Exception
import java.io.IOException
import java.io.ByteArrayInputStream
import java.lang.ClassNotFoundException
import me.kaotich00.fwauctionhouse.FwAuctionHouse
import me.kaotich00.fwauctionhouse.locale.LocalizationManager
import java.lang.RuntimeException
import me.kaotich00.fwauctionhouse.storage.util.StorageCredentials
import me.kaotich00.fwauctionhouse.storage.sql.hikari.HikariConnectionFactory
import com.zaxxer.hikari.HikariConfig
import me.kaotich00.fwauctionhouse.storage.sql.ConnectionFactory
import com.zaxxer.hikari.HikariDataSource
import java.lang.LinkageError
import java.sql.SQLException
import me.kaotich00.fwauctionhouse.storage.StorageMethod
import java.sql.PreparedStatement
import me.kaotich00.fwauctionhouse.storage.sql.SqlStorage
import me.kaotich00.fwauctionhouse.utils.SerializationUtil
import me.kaotich00.fwauctionhouse.objects.PendingSell
import me.kaotich00.fwauctionhouse.objects.PendingToken
import java.sql.DriverManager
import me.kaotich00.fwauctionhouse.storage.StorageFactory
import me.kaotich00.fwauctionhouse.storage.sql.hikari.MySQLConnectionFactory
import me.kaotich00.fwauctionhouse.commands.api.UserCommand
import java.util.concurrent.CompletableFuture
import me.kaotich00.fwauctionhouse.services.SimpleMarketService
import java.lang.Runnable
import me.kaotich00.fwauctionhouse.utils.ListingStatus
import me.kaotich00.fwauctionhouse.utils.CommandUtils
import me.kaotich00.fwauctionhouse.commands.user.SellCommand
import me.kaotich00.fwauctionhouse.commands.user.ConfirmCommand
import me.kaotich00.fwauctionhouse.commands.user.DeclineCommand
import me.kaotich00.fwauctionhouse.commands.user.ValidateTokenCommand
import net.md_5.bungee.api.chat.ClickEvent
import net.md_5.bungee.api.chat.HoverEvent
import net.md_5.bungee.api.chat.ComponentBuilder
import me.kaotich00.fwauctionhouse.commands.MarketCommandManager
import me.kaotich00.fwauctionhouse.message.Message
import net.md_5.bungee.api.ChatColor
import net.md_5.bungee.api.chat.TextComponent
import net.milkbowl.vault.economy.Economy
import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin
import java.util.*

class SimpleMarketService private constructor() {
    private val pendingSells: MutableSet<PendingSell>
    private val pendingTokens: MutableSet<PendingToken>
    fun scheduleSellingTask() {
        val simpleMarketService = getInstance()
        Bukkit.getScheduler().scheduleSyncRepeatingTask(
            JavaPlugin.getPlugin(
                FwAuctionHouse::class.java
            ), {
                CompletableFuture.supplyAsync {
                    val pendingSellList = StorageFactory.getInstance().storageMethod.pendingSells
                    pendingSellList
                }.thenAccept { pendingSells: List<PendingSell> ->
                    for (pendingSell in pendingSells) {
                        val player = Bukkit.getPlayer(pendingSell.buyerName)
                        if (player == null) {
                            val offlinePlayer = Bukkit.getOfflinePlayerIfCached(pendingSell.buyerName)
                            if (offlinePlayer == null) {
                                StorageFactory.getInstance().storageMethod.updateListingStatus(
                                    pendingSell.listingId,
                                    ListingStatus.NO_USER_FOUND
                                )
                            }
                            continue
                        }
                        if (simpleMarketService!!.getPendingSell(pendingSell.listingId).isPresent) {
                            continue
                        }
                        if (player.inventory.firstEmpty() == -1) {
                            Message.INVENTORY_FULL.send(player)
                            continue
                        }
                        if (FwAuctionHouse.Companion.getEconomy().getBalance(player) < pendingSell.totalCost) {
                            Message.NOT_ENOUGH_MONEY.send(player)
                            StorageFactory.getInstance().storageMethod.updateListingStatus(
                                pendingSell.listingId,
                                ListingStatus.NOT_ENOUGH_MONEY
                            )
                            continue
                        }
                        simpleMarketService.addToPendingSells(pendingSell)
                        val confirmPurchase = TextComponent("[CLICK HERE TO CONFIRM]")
                        confirmPurchase.color = ChatColor.GREEN
                        confirmPurchase.clickEvent =
                            ClickEvent(ClickEvent.Action.RUN_COMMAND, "/market confirm " + pendingSell.listingId)
                        confirmPurchase.hoverEvent = HoverEvent(
                            HoverEvent.Action.SHOW_TEXT, ComponentBuilder("Click to accept the purchase").color(
                                ChatColor.GREEN
                            ).italic(true).create()
                        )
                        val declinePurchase = TextComponent("[CLICK HERE TO DECLINE]\n")
                        declinePurchase.color = ChatColor.RED
                        declinePurchase.clickEvent =
                            ClickEvent(ClickEvent.Action.RUN_COMMAND, "/market decline " + pendingSell.listingId)
                        declinePurchase.hoverEvent = HoverEvent(
                            HoverEvent.Action.SHOW_TEXT, ComponentBuilder("Click to decline the purchase").color(
                                ChatColor.RED
                            ).italic(true).create()
                        )
                        val message = ComponentBuilder()
                        message
                            .append(
                                Message.PURCHASE_MESSAGE.asString(
                                    pendingSell.itemStack.i18NDisplayName,
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
        val simpleMarketService = getInstance()
        Bukkit.getScheduler().scheduleSyncRepeatingTask(
            JavaPlugin.getPlugin(
                FwAuctionHouse::class.java
            ), {
                CompletableFuture.supplyAsync {
                    val pendingTokens = StorageFactory.getInstance().storageMethod.pendingTokens
                    pendingTokens
                }.thenAccept { pendingTokens: List<PendingToken> ->
                    for (pendingToken in pendingTokens) {
                        val player = Bukkit.getPlayer(pendingToken.username) ?: continue
                        if (simpleMarketService!!.getPendingToken(pendingToken.sessionId).isPresent) {
                            continue
                        }
                        simpleMarketService.addToPendingToken(pendingToken)
                        val confirmPurchase = TextComponent("[CLICK HERE TO CONFIRM YOUR IDENTITY]\n")
                        confirmPurchase.color = ChatColor.GREEN
                        confirmPurchase.clickEvent =
                            ClickEvent(ClickEvent.Action.RUN_COMMAND, "/market validateToken " + pendingToken.sessionId)
                        confirmPurchase.hoverEvent = HoverEvent(
                            HoverEvent.Action.SHOW_TEXT, ComponentBuilder("Click to validate your identity").color(
                                ChatColor.GREEN
                            ).italic(true).create()
                        )
                        val message = ComponentBuilder()
                        message
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
        pendingSells.add(pendingSell)
    }

    fun removeFromPendingSells(pendingSell: PendingSell) {
        pendingSells.remove(pendingSell)
    }

    fun getPendingSell(id: Int): Optional<PendingSell> {
        return pendingSells.stream().filter { pendingSell: PendingSell -> pendingSell.listingId == id }.findFirst()
    }

    fun addToPendingToken(pendingToken: PendingToken) {
        pendingTokens.add(pendingToken)
    }

    fun removeFromPendingToken(pendingToken: PendingToken) {
        pendingTokens.remove(pendingToken)
    }

    fun getPendingToken(id: Int): Optional<PendingToken> {
        return pendingTokens.stream().filter { pendingSell: PendingToken -> pendingSell.sessionId == id }.findFirst()
    }

    companion object {
        private var instance: SimpleMarketService? = null
        fun getInstance(): SimpleMarketService? {
            if (instance == null) {
                instance = SimpleMarketService()
            }
            return instance
        }
    }

    init {
        if (instance != null) {
            throw RuntimeException("Use getInstance() method to get the single instance of this class.")
        }
        pendingSells = HashSet()
        pendingTokens = HashSet()
    }
}