package me.kaotich00.fwauctionhouse.message;

import me.kaotich00.fwauctionhouse.FwAuctionHouse;
import me.kaotich00.fwauctionhouse.locale.LocalizationManager;
import me.kaotich00.fwauctionhouse.utils.MessageUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public enum Message {

    PREFIX("fwauctionhouse_prefix", false),

    HELP_MESSAGE("help_message", false),
    SOLD_ITEM("sold_item", true),
    CANNOT_SELL_AIR("cannot_sell_air", true);

    private final String message;
    private final boolean showPrefix;
    private final LocalizationManager localizationManager;

    Message(String message, boolean showPrefix) {
        this.message = MessageUtils.rewritePlaceholders(message);
        this.showPrefix = showPrefix;
        this.localizationManager = FwAuctionHouse.getLocalizationManager();
    }

    public void send(CommandSender sender, Object... objects) {
        sender.sendMessage(asString(objects));
    }

    public void broadcast(Object... objects) {
        Bukkit.getServer().broadcastMessage(asString(objects));
    }

    public String asString(Object... objects) {
        return format(objects);
    }

    private String format(Object... objects) {
        String string = localizationManager.localize(this.message);
        if(this.showPrefix) {
            string = localizationManager.localize(PREFIX.message) + " " + string;
        }
        for (int i = 0; i < objects.length; i++) {
            Object o = objects[i];
            string = string.replace("{" + i + "}", String.valueOf(o));
        }
        return ChatColor.translateAlternateColorCodes('&', string);
    }

    public static LocalizationManager getLocalizationManager() {
        return LocalizationManager.getInstance(FwAuctionHouse.getPlugin(FwAuctionHouse.class));
    }

}
