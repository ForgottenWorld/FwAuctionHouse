package me.kaotich00.fwauctionhouse.commands.user

import me.kaotich00.fwauctionhouse.commands.api.UserCommand
import me.kaotich00.fwauctionhouse.message.Message
import me.kaotich00.fwauctionhouse.storage.StorageFactory
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import java.util.concurrent.CompletableFuture

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
        val storage = StorageFactory.instance
        CompletableFuture.supplyAsync {
            storage?.storageMethod?.insertListing(player, itemToSell, unitPrice)
            true
        }.thenAccept { result: Boolean? ->
            player.playSound(player.location, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1f, 1f)
            Message.SOLD_ITEM.send(sender, itemToSell.i18NDisplayName, itemToSell.amount, unitPrice)
            val slot = playerInventory.heldItemSlot
            playerInventory.setItem(slot, ItemStack(Material.AIR))
        }
    }

    override val info: String?
        get() = super.info
    override val usage: String?
        get() = "/market sell <price>"
    override val name: String?
        get() = super.name
    override val requiredArgs: Int?
        get() = 1
}