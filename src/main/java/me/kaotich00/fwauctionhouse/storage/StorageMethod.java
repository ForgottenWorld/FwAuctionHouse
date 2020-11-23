package me.kaotich00.fwauctionhouse.storage;

import me.kaotich00.fwauctionhouse.FwAuctionHouse;
import me.kaotich00.fwauctionhouse.objects.PendingSell;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public interface StorageMethod {

    FwAuctionHouse getPlugin();

    void init();

    void shutdown();

    Connection getConnection() throws SQLException;

    void insertListing(Player seller, ItemStack itemStack, Float unitPrice);

    List<PendingSell> getPendingSells();

    void updateListingStatus(int listingId, int status);

    void deletePendingSell(int listingId);

}
