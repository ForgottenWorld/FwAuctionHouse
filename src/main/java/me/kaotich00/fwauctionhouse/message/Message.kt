package me.kaotich00.fwauctionhouse.message

import me.kaotich00.fwauctionhouse.locale.LocalizationManager
import me.kaotich00.fwauctionhouse.utils.MessageUtils
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.command.CommandSender

enum class Message(
    message: String,
    private val showPrefix: Boolean
) {
    PREFIX("fwauctionhouse_prefix", false),
    HELP_MESSAGE("help_message", false),
    SOLD_ITEM("sold_item", true),
    BOUGHT_ITEM("bought_item", true),
    INVENTORY_FULL("inventory_full", true),
    NOT_ENOUGH_MONEY("not_enough_money",true),
    CANNOT_SELL_AIR("cannot_sell_air", true),
    PURCHASE_MESSAGE("purchase_message",false),
    VALIDATED_TOKEN("validated_token", true),
    VALIDATED_TOKEN_MESSAGE("validate_token_message",true),
    DECLINED("declined", true);

    private val message = MessageUtils.rewritePlaceholders(message)

    fun send(sender: CommandSender, vararg objects: Any) {
        sender.sendMessage(asString(*objects))
    }

    fun broadcast(vararg objects: Any) {
        Bukkit.getServer().broadcastMessage(asString(*objects))
    }

    fun asString(vararg objects: Any): String {
        return format(objects)
    }

    private fun format(objects: Array<out Any>): String {
        var string = LocalizationManager.localize(message)
        for ((i,o) in objects.withIndex()) {
            string = string.replace("{$i}", o.toString())
        }
        if (showPrefix) {
            string = "${LocalizationManager.localize(PREFIX.message)} $string"
        }
        return ChatColor.translateAlternateColorCodes('&', string)
    }

}