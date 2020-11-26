package me.kaotich00.fwauctionhouse.services;

import me.kaotich00.fwauctionhouse.FwAuctionHouse;
import me.kaotich00.fwauctionhouse.message.Message;
import me.kaotich00.fwauctionhouse.objects.PendingSell;
import me.kaotich00.fwauctionhouse.storage.StorageFactory;
import me.kaotich00.fwauctionhouse.utils.ListingStatus;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.concurrent.CompletableFuture;

public class SimpleMarketService {

    private static SimpleMarketService instance;
    private Set<PendingSell> pendingSells;

    private SimpleMarketService() {
        if (instance != null){
            throw new RuntimeException("Use getInstance() method to get the single instance of this class.");
        }
        this.pendingSells = new HashSet<>();
    }

    public static SimpleMarketService getInstance() {
        if(instance == null) {
            instance = new SimpleMarketService();
        }
        return instance;
    }

    public void scheduleSellingTask() {
        SimpleMarketService simpleMarketService = SimpleMarketService.getInstance();

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

                    if(simpleMarketService.getPendingSell(pendingSell.getListingId()).isPresent()) {
                        continue;
                    }

                    if (player.getInventory().firstEmpty() == -1) {
                        Message.INVENTORY_FULL.send(player);
                        continue;
                    }

                    if (FwAuctionHouse.getEconomy().getBalance(player) < pendingSell.getTotalCost()) {
                        Message.NOT_ENOUGH_MONEY.send(player);
                        StorageFactory.getInstance().getStorageMethod().updateListingStatus(pendingSell.getListingId(), ListingStatus.NOT_ENOUGH_MONEY);
                        continue;
                    }

                    simpleMarketService.addToPendingSells(pendingSell);

                    TextComponent confirmPurchase = new TextComponent("[CLICK HERE TO CONFIRM]");
                    confirmPurchase.setColor(ChatColor.GREEN);
                    confirmPurchase.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/market confirm " + pendingSell.getListingId()));
                    confirmPurchase.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Click to accept the purchase").color(ChatColor.GREEN).italic(true).create()));

                    TextComponent declinePurchase = new TextComponent("[CLICK HERE TO DECLINE]\n");
                    declinePurchase.setColor(ChatColor.RED);
                    declinePurchase.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/market decline " + pendingSell.getListingId()));
                    declinePurchase.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Click to decline the purchase").color(ChatColor.RED).italic(true).create()));

                    ComponentBuilder message = new ComponentBuilder();
                    message
                            .append(Message.PURCHASE_MESSAGE.asString(pendingSell.getItemStack().getI18NDisplayName(), pendingSell.getItemStack().getAmount()))
                            .append(confirmPurchase)
                            .append(" ")
                            .append(declinePurchase)
                            .append(ChatColor.GREEN + "" + ChatColor.STRIKETHROUGH + ChatColor.BOLD + "\n------------------------------------------" );

                    player.spigot().sendMessage(message.create());
                }
            });
        }, 20, 20);
    }

    public void addToPendingSells(PendingSell pendingSell) {
        this.pendingSells.add(pendingSell);
    }

    public void removeFromPendingSells(PendingSell pendingSell) {
        this.pendingSells.remove(pendingSell);
    }

    public Optional<PendingSell> getPendingSell(int id) {
        return this.pendingSells.stream().filter(pendingSell -> pendingSell.getListingId() == id).findFirst();
    }

}
