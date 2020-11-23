package me.kaotich00.fwauctionhouse.services;

import me.kaotich00.fwauctionhouse.FwAuctionHouse;
import me.kaotich00.fwauctionhouse.message.Message;
import me.kaotich00.fwauctionhouse.objects.PendingSell;
import me.kaotich00.fwauctionhouse.storage.Storage;
import me.kaotich00.fwauctionhouse.storage.StorageFactory;
import me.kaotich00.fwauctionhouse.utils.ListingStatus;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class SimpleMarketService {

    private static SimpleMarketService instance;

    private SimpleMarketService() {
        if (instance != null){
            throw new RuntimeException("Use getInstance() method to get the single instance of this class.");
        }
    }

    public static SimpleMarketService getInstance() {
        if(instance == null) {
            instance = new SimpleMarketService();
        }
        return instance;
    }

    public void scheduleSellingTask() {
        Bukkit.getScheduler().scheduleSyncRepeatingTask(FwAuctionHouse.getPlugin(FwAuctionHouse.class), () -> {
            CompletableFuture.supplyAsync(() -> {
                List<PendingSell> pendingSellList = StorageFactory.getInstance().getStorageMethod().getPendingSells();
                return pendingSellList;
            }).thenAccept(pendingSells -> {
                for(PendingSell pendingSell: pendingSells) {
                    Player player = Bukkit.getPlayer(pendingSell.getBuyerName());

                    if(player == null) {
                        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayerIfCached(pendingSell.getBuyerName());
                        if(offlinePlayer == null) {
                            StorageFactory.getInstance().getStorageMethod().updateListingStatus(pendingSell.getListingId(), ListingStatus.NO_USER_FOUND);
                        }
                        continue;
                    }

                    if(player.getInventory().firstEmpty() == -1) {
                        continue;
                    }

                    if(FwAuctionHouse.getEconomy().getBalance(player) < pendingSell.getTotalCost()) {
                        StorageFactory.getInstance().getStorageMethod().updateListingStatus(pendingSell.getListingId(), ListingStatus.NOT_ENOUGH_MONEY);
                        continue;
                    }

                    ItemStack boughtItem = pendingSell.getItemStack();
                    player.getInventory().addItem(boughtItem);

                    FwAuctionHouse.getEconomy().withdrawPlayer(player, pendingSell.getTotalCost());

                    Message.BOUGHT_ITEM.send(player, boughtItem.getI18NDisplayName(), boughtItem.getAmount(), pendingSell.getTotalCost());
                    player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP,1,1);

                    CompletableFuture.runAsync(() -> {
                        StorageFactory.getInstance().getStorageMethod().deletePendingSell(pendingSell.getListingId());
                    });
                }
            });
        }, 300, 300);
    }

}
