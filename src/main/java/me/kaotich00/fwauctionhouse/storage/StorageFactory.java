package me.kaotich00.fwauctionhouse.storage;

import me.kaotich00.fwauctionhouse.FwAuctionHouse;
import me.kaotich00.fwauctionhouse.storage.sql.SqlStorage;
import me.kaotich00.fwauctionhouse.storage.sql.hikari.MySQLConnectionFactory;
import me.kaotich00.fwauctionhouse.storage.util.StorageCredentials;
import org.bukkit.configuration.file.FileConfiguration;

public class StorageFactory {

    public static Storage storage;

    public static Storage getInstance() {
        if( storage != null ) {
            return storage;
        }
        storage = new Storage(FwAuctionHouse.getPlugin(FwAuctionHouse.class), getStorageFromConfig());
        return storage;
    }

    private static StorageMethod getStorageFromConfig() {
        FileConfiguration defaultConfig = FwAuctionHouse.getDefaultConfig();
        String host = defaultConfig.getString("address");
        String database = defaultConfig.getString("database");
        String username = defaultConfig.getString("username");
        String password = defaultConfig.getString("password");
        StorageCredentials credentials = new StorageCredentials(host,database,username,password);

        return new SqlStorage(FwAuctionHouse.getPlugin(FwAuctionHouse.class), new MySQLConnectionFactory(credentials));
    }

}
