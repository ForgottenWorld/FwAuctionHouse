package me.kaotich00.fwauctionhouse.storage;

import me.kaotich00.fwauctionhouse.FwAuctionHouse;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.sql.Connection;
import java.sql.SQLException;

public interface StorageMethod {

    FwAuctionHouse getPlugin();

    void init();

    void shutdown();

    Connection getConnection() throws SQLException;

    void insertListing(Player seller, ItemStack itemStack, Float unitPrice);

}
