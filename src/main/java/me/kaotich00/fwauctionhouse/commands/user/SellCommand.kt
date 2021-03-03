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
import java.util.HashMap
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
import java.util.HashSet
import me.kaotich00.fwauctionhouse.commands.MarketCommandManager
import me.kaotich00.fwauctionhouse.message.Message
import net.milkbowl.vault.economy.Economy
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

class SellCommand : UserCommand() {
    override fun onCommand(sender: CommandSender, args: Array<String>) {
        super.onCommand(sender, args)
        val sellPrice = args[1]
        val player = sender as Player
        val playerInventory = player.inventory
        val itemToSell = playerInventory.itemInMainHand
        if (itemToSell.type == Material.AIR) {
            Message.CANNOT_SELL_AIR.send(sender)
            return
        }
        val unitPrice = sellPrice.toFloat()
        val storage = StorageFactory.getInstance()
        CompletableFuture.supplyAsync {
            storage.storageMethod.insertListing(player, itemToSell, unitPrice)
            true
        }.thenAccept { result: Boolean? ->
            player.playSound(player.location, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1f, 1f)
            Message.SOLD_ITEM.send(sender, itemToSell.i18NDisplayName, itemToSell.amount, unitPrice)
            val slot = playerInventory.heldItemSlot
            playerInventory.setItem(slot, ItemStack(Material.AIR))
        }
    }

    override val info: String?
        get() = super.getInfo()
    override val usage: String?
        get() = "/market sell <price>"
    override val name: String?
        get() = super.getName()
    override val requiredArgs: Int?
        get() = 1
}