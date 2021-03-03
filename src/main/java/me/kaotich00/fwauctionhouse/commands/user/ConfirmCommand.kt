package me.kaotich00.fwauctionhouse.commands.user

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
import net.milkbowl.vault.economy.Economy
import org.bukkit.Sound
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import java.util.*

class ConfirmCommand : UserCommand() {
    override fun onCommand(sender: CommandSender, args: Array<String>) {
        super.onCommand(sender, args)
        val buyer = sender as Player
        val listingId = args[1]
        val optPendingSell: Optional<PendingSell> =
            SimpleMarketService.Companion.getInstance()!!.getPendingSell(listingId.toInt())
        if (optPendingSell.isPresent) {
            val pendingSell = optPendingSell.get()
            val boughtItem = pendingSell.itemStack
            buyer.inventory.addItem(boughtItem)
            FwAuctionHouse.Companion.getEconomy().withdrawPlayer(buyer, pendingSell.totalCost.toDouble())
            Message.BOUGHT_ITEM.send(buyer, boughtItem!!.i18NDisplayName, boughtItem.amount, pendingSell.totalCost)
            buyer.playSound(buyer.location, Sound.ENTITY_PLAYER_LEVELUP, 1f, 1f)
            CompletableFuture.runAsync {
                SimpleMarketService.Companion.getInstance()!!.removeFromPendingSells(pendingSell)
                StorageFactory.getInstance().storageMethod.deletePendingSell(pendingSell.listingId)
            }
        }
    }

    override val info: String?
        get() = super.getInfo()
    override val usage: String?
        get() = "/market confirm <listingId>"
    override val name: String?
        get() = super.getName()
    override val requiredArgs: Int?
        get() = 1
}