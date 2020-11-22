package me.kaotich00.fwauctionhouse.commands.api;

import org.bukkit.command.CommandSender;

public interface Command {

    void onCommand(CommandSender sender, String args[]);

    String getName();

    String getUsage();

    String getInfo();

    Integer getRequiredArgs();

}
