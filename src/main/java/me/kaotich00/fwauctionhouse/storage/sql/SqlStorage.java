package me.kaotich00.fwauctionhouse.storage.sql;

import me.kaotich00.fwauctionhouse.FwAuctionHouse;
import me.kaotich00.fwauctionhouse.storage.StorageMethod;
import me.kaotich00.fwauctionhouse.utils.SerializationUtil;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class SqlStorage implements StorageMethod {

    private static final String INSERT_LISTING = "INSERT INTO listing(`seller_uuid`,`seller_nickname`,`amount`,`unit_price`,`status`,`item_stack`,`additional_data`,`minecraft_enum`,`item_name`) VALUES (?,?,?,?,1,?,?,?,?)";

    private ConnectionFactory connectionFactory;
    private final FwAuctionHouse plugin;

    public SqlStorage(FwAuctionHouse plugin, ConnectionFactory connectionFactory) {
        this.connectionFactory = connectionFactory;
        this.plugin = plugin;
    }

    @Override
    public FwAuctionHouse getPlugin() {
        return this.plugin;
    }

    @Override
    public void init() {
        this.connectionFactory.init(this.plugin);
    }

    @Override
    public void shutdown() {
        try {
            this.connectionFactory.shutdown();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public Connection getConnection() throws SQLException {
        return this.connectionFactory.getConnection();
    }

    public void insertListing(Player seller, ItemStack itemStack, Float unitPrice) {
        try (Connection c = getConnection()) {
            PreparedStatement insertListing = c.prepareStatement(INSERT_LISTING);
            insertListing.setString(1, seller.getUniqueId().toString());
            insertListing.setString(2, seller.getName());
            insertListing.setInt(3, itemStack.getAmount());
            insertListing.setFloat(4, unitPrice);
            insertListing.setString(5, SerializationUtil.toBase64(itemStack));
            insertListing.setString(6, itemStack.getI18NDisplayName());
            insertListing.setString(7, itemStack.getType().toString());
            insertListing.setString(8, itemStack.getI18NDisplayName());
            insertListing.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
