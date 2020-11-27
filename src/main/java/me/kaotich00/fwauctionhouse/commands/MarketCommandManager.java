package me.kaotich00.fwauctionhouse.commands;

import me.kaotich00.fwauctionhouse.FwAuctionHouse;
import me.kaotich00.fwauctionhouse.commands.api.Command;
import me.kaotich00.fwauctionhouse.commands.user.ConfirmCommand;
import me.kaotich00.fwauctionhouse.commands.user.DeclineCommand;
import me.kaotich00.fwauctionhouse.commands.user.SellCommand;
import me.kaotich00.fwauctionhouse.commands.user.ValidateTokenCommand;
import me.kaotich00.fwauctionhouse.message.Message;
import me.kaotich00.fwauctionhouse.utils.CommandUtils;
import me.kaotich00.fwauctionhouse.utils.MessageUtils;
import me.kaotich00.fwauctionhouse.utils.NameUtil;
import org.bukkit.command.CommandException;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MarketCommandManager implements TabExecutor {

    private Map<String, Command> commandRegistry;
    private FwAuctionHouse plugin;

    public MarketCommandManager(FwAuctionHouse plugin) {
        this.commandRegistry = new HashMap<>();
        this.plugin = plugin;
        setup();
    }

    private void setup() {
        this.commandRegistry.put(CommandUtils.SELL_COMMAND, new SellCommand());
        this.commandRegistry.put(CommandUtils.CONFIRM_COMMAND, new ConfirmCommand());
        this.commandRegistry.put(CommandUtils.DECLINE_COMMAND, new DeclineCommand());
        this.commandRegistry.put(CommandUtils.VALIDATE_TOKEN_COMMAND, new ValidateTokenCommand());
    }

    private Command getCommand(String name) {
        return this.commandRegistry.get(name);
    }

    @Override
    public boolean onCommand(CommandSender sender, org.bukkit.command.Command command, String label, String[] args) {
        if( args.length == 0 ) {
            Message.HELP_MESSAGE.send(sender);
            return CommandUtils.COMMAND_SUCCESS;
        }

        Command fwCommand = getCommand(args[0]);

        if( fwCommand != null ) {
            if(fwCommand.getRequiredArgs() > args.length) {
                sender.sendMessage(MessageUtils.formatErrorMessage("Not enough arguments"));
                sender.sendMessage(MessageUtils.formatErrorMessage(fwCommand.getUsage()));
                return true;
            }
            try {
                fwCommand.onCommand(sender, args);
            } catch (CommandException e) {
            }
        }

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, org.bukkit.command.Command command, String alias, String[] args) {
        List<String> suggestions = new ArrayList<>();
        String argsIndex = "";

        /* Suggest child commands */
        if(args.length == 1) {
            argsIndex = args[0];

            for(String commandName: this.commandRegistry.keySet()) {
                suggestions.add(commandName);
            }
        }

        return NameUtil.filterByStart(suggestions, argsIndex);
    }

}
