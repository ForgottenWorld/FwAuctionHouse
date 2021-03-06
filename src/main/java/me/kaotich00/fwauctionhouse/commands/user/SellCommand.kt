package me.kaotich00.fwauctionhouse.commands.user

import me.kaotich00.fwauctionhouse.commands.api.UserCommand
import me.kaotich00.fwauctionhouse.message.Message
import me.kaotich00.fwauctionhouse.storage.StorageProvider
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import java.util.concurrent.CompletableFuture

class SellCommand : UserCommand(
    "sell",
    "",
    1,
    "/market sell <price>"
) {
    override fun doCommand(sender: Player, args: Array<String>) {

        val sellPrice = args[1].toDoubleOrNull() ?: run {
            sender.sendMessage("You must insert a valid amount")
            return
        }

        val playerInventory = sender.inventory
        val itemToSell = playerInventory.itemInMainHand
        if (itemToSell.type == Material.AIR) {
            Message.CANNOT_SELL_AIR.send(sender)
            return
        }

        val storage = StorageProvider.storageInstance
        CompletableFuture.supplyAsync {
            storage.storageMethod.insertListing(sender, itemToSell, sellPrice)
            true
        }.thenAccept {
            sender.playSound(sender.location, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1f, 1f)
            Message.SOLD_ITEM.send(sender, itemToSell.i18NDisplayName, itemToSell.amount, sellPrice)
            playerInventory.setItem(playerInventory.heldItemSlot, ItemStack(Material.AIR))
        }
    }

}