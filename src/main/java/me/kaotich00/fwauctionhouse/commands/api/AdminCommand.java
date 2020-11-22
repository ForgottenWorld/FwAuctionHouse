package me.kaotich00.fwauctionhouse.commands.api;

import me.kaotich00.fwauctionhouse.utils.MessageUtils;
import org.bukkit.command.CommandException;
import org.bukkit.command.CommandSender;

public class AdminCommand extends UserCommand {

    @Override
    public void onCommand(CommandSender sender, String[] args) {
        if(!sender.hasPermission("fwauctionhouse.admin")) {
            sender.sendMessage(MessageUtils.formatErrorMessage("You don't have permissions to run this command"));
            throw new CommandException();
        }
    }

}
