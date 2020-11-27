package me.kaotich00.fwauctionhouse.storage.sql;

import me.kaotich00.fwauctionhouse.FwAuctionHouse;
import me.kaotich00.fwauctionhouse.objects.PendingSell;
import me.kaotich00.fwauctionhouse.objects.PendingToken;
import me.kaotich00.fwauctionhouse.storage.StorageMethod;
import me.kaotich00.fwauctionhouse.utils.SerializationUtil;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SqlStorage implements StorageMethod {

    private static final String INSERT_LISTING = "INSERT INTO listing(`seller_uuid`,`seller_nickname`,`amount`,`unit_price`,`status`,`item_stack`,`additional_data`,`minecraft_enum`,`item_name`) VALUES (?,?,?,?,1,?,?,?,?)";
    private static final String SELECT_PENDING_SELLS = "SELECT * FROM listing WHERE status = 2";
    private static final String UPDATE_LISTING_STATUS = "UPDATE listing SET status = ? WHERE id = ?";
    private static final String DELETE_PENDING_SELLS = "DELETE FROM listing WHERE id = ?";

    private static final String SELECT_PENDING_TOKENS = "SELECT * FROM player_session WHERE token IS NOT NULL AND (is_validated IS NULL OR is_validated <> 1)";
    private static final String VALIDATE_TOKEN = "UPDATE player_session SET is_validated = 1 WHERE id = ?";

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

    @Override
    public List<PendingSell> getPendingSells() {
        List<PendingSell> pendingSellList = new ArrayList<>();
        try (Connection c = getConnection()) {
            try (PreparedStatement ps = c.prepareStatement(SELECT_PENDING_SELLS)) {
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        int listingId = rs.getInt("id");
                        String buyerName = rs.getString("buyer_name");

                        int amount = rs.getInt("amount");
                        Float totalCost = rs.getFloat("unit_price") * amount;

                        String itemType = rs.getString("item_stack");

                        ItemStack itemStack = SerializationUtil.fromBase64(itemType);
                        if(buyerName != null && itemStack != null) {
                            PendingSell pendingSell = new PendingSell(listingId, itemStack, buyerName, totalCost);
                            pendingSellList.add(pendingSell);
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return pendingSellList;
    }

    @Override
    public void updateListingStatus(int listingId, int status) {
        try (Connection c = getConnection()) {
            PreparedStatement updateListing = c.prepareStatement(UPDATE_LISTING_STATUS);
            updateListing.setInt(1, status);
            updateListing.setInt(2, listingId);
            updateListing.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void deletePendingSell(int listingId) {
        try (Connection c = getConnection()) {
            PreparedStatement updateListing = c.prepareStatement(DELETE_PENDING_SELLS);
            updateListing.setInt(1, listingId);
            updateListing.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<PendingToken> getPendingTokens() {
        List<PendingToken> pendingTokens = new ArrayList<>();
        try (Connection c = getConnection()) {
            try (PreparedStatement ps = c.prepareStatement(SELECT_PENDING_TOKENS)) {
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        int sessionId = rs.getInt("id");
                        String username = rs.getString("username");
                        String token = rs.getString("token");

                        PendingToken pendingToken = new PendingToken(sessionId, username, token);
                        pendingTokens.add(pendingToken);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return pendingTokens;
    }

    @Override
    public void validateToken(int sessionId) {
        try (Connection c = getConnection()) {
            PreparedStatement updateListing = c.prepareStatement(VALIDATE_TOKEN);
            updateListing.setInt(1, sessionId);
            updateListing.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
