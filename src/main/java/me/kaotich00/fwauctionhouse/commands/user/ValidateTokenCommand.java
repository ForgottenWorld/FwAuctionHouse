package me.kaotich00.fwauctionhouse.commands.user;

import me.kaotich00.fwauctionhouse.commands.api.UserCommand;
import me.kaotich00.fwauctionhouse.message.Message;
import me.kaotich00.fwauctionhouse.objects.PendingToken;
import me.kaotich00.fwauctionhouse.services.SimpleMarketService;
import me.kaotich00.fwauctionhouse.storage.StorageFactory;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Optional;

public class ValidateTokenCommand extends UserCommand {

    @Override
    public void onCommand(CommandSender sender, String[] args) {
        super.onCommand(sender, args);

        String sessionId = args[1];

        Optional<PendingToken> optPendingToken = SimpleMarketService.getInstance().getPendingToken(Integer.parseInt(sessionId));
        if(optPendingToken.isPresent()) {
            PendingToken pendingSell = optPendingToken.get();
            Message.VALIDATED_TOKEN.send(sender);
            StorageFactory.getInstance().getStorageMethod().validateToken(Integer.parseInt(sessionId));
            SimpleMarketService.getInstance().removeFromPendingToken(pendingSell);
        }

    }

    @Override
    public String getInfo() {
        return super.getInfo();
    }

    @Override
    public String getUsage() {
        return "/market validateToken <sessionId>";
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
