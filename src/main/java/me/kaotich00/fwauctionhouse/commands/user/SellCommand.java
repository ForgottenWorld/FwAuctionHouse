package me.kaotich00.fwauctionhouse.commands.user;

import me.kaotich00.fwauctionhouse.FwAuctionHouse;
import me.kaotich00.fwauctionhouse.commands.api.UserCommand;
import me.kaotich00.fwauctionhouse.message.Message;
import me.kaotich00.fwauctionhouse.storage.Storage;
import me.kaotich00.fwauctionhouse.storage.StorageFactory;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.concurrent.CompletableFuture;

public class SellCommand extends UserCommand {

    @Override
    public void onCommand(CommandSender sender, String[] args) {
        super.onCommand(sender, args);

        String sellPrice = args[1];

        Player player = (Player) sender;
        PlayerInventory playerInventory = player.getInventory();
        ItemStack itemToSell = playerInventory.getItemInMainHand();

        if(itemToSell.getType().equals(Material.AIR)) {
            Message.CANNOT_SELL_AIR.send(sender);
            return;
        }

        Float unitPrice = Float.parseFloat(sellPrice);

        Storage storage = StorageFactory.getInstance();
        CompletableFuture.supplyAsync(() -> {
            storage.getStorageMethod().insertListing(player, itemToSell, unitPrice);
            return true;
        }).thenAccept((result) -> {
            player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP,1,1);
            Message.SOLD_ITEM.send(sender, itemToSell.getI18NDisplayName(), itemToSell.getAmount(), unitPrice);
            int slot = playerInventory.getHeldItemSlot();
            playerInventory.setItem(slot, new ItemStack(Material.AIR));
        });
    }

    @Override
    public String getInfo() {
        return super.getInfo();
    }

    @Override
    public String getUsage() {
        return "/market sell <price>";
    }

    @Override
    public String getName() {
        return super.getName();
    }

    @Override
    public Integer getRequiredArgs() {
        return 1;
    }

}
