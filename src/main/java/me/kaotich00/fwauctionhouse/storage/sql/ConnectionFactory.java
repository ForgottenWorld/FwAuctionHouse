package me.kaotich00.fwauctionhouse.storage.sql;

import me.kaotich00.fwauctionhouse.FwAuctionHouse;

import java.sql.Connection;
import java.sql.SQLException;

public interface ConnectionFactory {

    void init(FwAuctionHouse plugin);

    void shutdown() throws Exception;

    Connection getConnection() throws SQLException;

}
