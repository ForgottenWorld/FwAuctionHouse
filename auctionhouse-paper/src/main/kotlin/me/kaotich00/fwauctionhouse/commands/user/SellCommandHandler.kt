package me.kaotich00.fwauctionhouse.commands.user

import com.google.inject.Inject
import kotlinx.coroutines.withContext
import me.kaotich00.fwauctionhouse.commands.api.PlayerCommandHandler
import me.kaotich00.fwauctionhouse.message.Message
import me.kaotich00.fwauctionhouse.services.MarketAreaService
import me.kaotich00.fwauctionhouse.services.WebApiService
import me.kaotich00.fwauctionhouse.storage.ListingsDao
import me.kaotich00.fwauctionhouse.utils.BukkitDispatchers
import me.kaotich00.fwauctionhouse.utils.launch
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

class SellCommandHandler @Inject constructor(
    private val listingsDao: ListingsDao,
    private val marketAreaService: MarketAreaService,
    private val webApiService: WebApiService
) : PlayerCommandHandler(
    name = "sell",
    requiredArgs = 1,
    usage = "/market sell <price>",
    info = "Sell the item you're holding for the specified price"
) {
    override fun onCommand(sender: Player, args: Array<String>) {
        val sellPrice = args[1].toDoubleOrNull() ?: run {
            Message.INSERT_VALID_AMOUNT.send(sender)
            return
        }

        if (!marketAreaService.canPlayerUseMarket(sender)) {
            Message.YOU_MAY_NOT_ACCESS_MARKET_HERE.send(sender)
            return
        }

        val playerInventory = sender.inventory
        val itemToSell = playerInventory.itemInMainHand

        if (itemToSell.type == Material.AIR) {
            Message.CANNOT_SELL_AIR.send(sender)
            return
        }

        launch {
            withContext(BukkitDispatchers.async) {
                listingsDao.insertListing(sender, itemToSell, sellPrice)
            }
            webApiService.sendOnListingsChangedEvent()
            sender.playSound(sender.location, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1f, 1f)
            Message.SOLD_ITEM.send(sender, itemToSell.i18NDisplayName ?: "N/D", itemToSell.amount, sellPrice)
            playerInventory.setItem(playerInventory.heldItemSlot, ItemStack(Material.AIR))
        }
    }

}