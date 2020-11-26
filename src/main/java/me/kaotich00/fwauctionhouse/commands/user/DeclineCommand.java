package me.kaotich00.fwauctionhouse.commands.user;

import me.kaotich00.fwauctionhouse.commands.api.UserCommand;
import me.kaotich00.fwauctionhouse.message.Message;
import me.kaotich00.fwauctionhouse.objects.PendingSell;
import me.kaotich00.fwauctionhouse.services.SimpleMarketService;
import me.kaotich00.fwauctionhouse.storage.StorageFactory;
import me.kaotich00.fwauctionhouse.utils.ListingStatus;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Optional;

public class DeclineCommand extends UserCommand {

    @Override
    public void onCommand(CommandSender sender, String[] args) {
        super.onCommand(sender, args);

        Player buyer = (Player) sender;

        String listingId = args[1];

        Optional<PendingSell> optPendingSell = SimpleMarketService.getInstance().getPendingSell(Integer.parseInt(listingId));
        if(optPendingSell.isPresent()) {
            PendingSell pendingSell = optPendingSell.get();
            Message.DECLINED.send(sender);
            StorageFactory.getInstance().getStorageMethod().updateListingStatus(pendingSell.getListingId(), ListingStatus.ORDER_AVAILABLE);
            SimpleMarketService.getInstance().removeFromPendingSells(pendingSell);
        }

    }

    @Override
    public String getInfo() {
        return super.getInfo();
    }

    @Override
    public String getUsage() {
        return "/market decline <listingId>";
    }

    @Override
    public String getName() {
        return super.getName();
    }

    @Override
    public Integer getRequiredArgs() {
        return 1;
    }

}
