package me.kaotich00.fwauctionhouse.commands.user;

import me.kaotich00.fwauctionhouse.FwAuctionHouse;
import me.kaotich00.fwauctionhouse.commands.api.UserCommand;
import me.kaotich00.fwauctionhouse.message.Message;
import me.kaotich00.fwauctionhouse.objects.PendingSell;
import me.kaotich00.fwauctionhouse.services.SimpleMarketService;
import me.kaotich00.fwauctionhouse.storage.StorageFactory;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class ConfirmCommand extends UserCommand {

    @Override
    public void onCommand(CommandSender sender, String[] args) {
        super.onCommand(sender, args);

        Player buyer = (Player) sender;

        String listingId = args[1];

        Optional<PendingSell> optPendingSell = SimpleMarketService.getInstance().getPendingSell(Integer.parseInt(listingId));
        if(optPendingSell.isPresent()) {
            PendingSell pendingSell = optPendingSell.get();

            ItemStack boughtItem = pendingSell.getItemStack();
            buyer.getInventory().addItem(boughtItem);

            FwAuctionHouse.getEconomy().withdrawPlayer(buyer, pendingSell.getTotalCost());

            Message.BOUGHT_ITEM.send(buyer, boughtItem.getI18NDisplayName(), boughtItem.getAmount(), pendingSell.getTotalCost());
            buyer.playSound(buyer.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 1);

            CompletableFuture.runAsync(() -> {
                SimpleMarketService.getInstance().removeFromPendingSells(pendingSell);
                StorageFactory.getInstance().getStorageMethod().deletePendingSell(pendingSell.getListingId());
            });
        }
    }

    @Override
    public String getInfo() {
        return super.getInfo();
    }

    @Override
    public String getUsage() {
        return "/market confirm <listingId>";
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
