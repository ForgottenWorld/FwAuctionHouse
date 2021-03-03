package me.kaotich00.fwauctionhouse.message

import me.kaotich00.fwauctionhouse.FwAuctionHouse
import me.kaotich00.fwauctionhouse.locale.LocalizationManager
import me.kaotich00.fwauctionhouse.utils.MessageUtils
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.command.CommandSender
import org.bukkit.plugin.java.JavaPlugin

enum class Message(message: String, showPrefix: Boolean) {
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

    private val message: String?
    private val showPrefix: Boolean
    private val localizationManager: LocalizationManager?
    fun send(sender: CommandSender, vararg objects: Any?) {
        sender.sendMessage(asString(*objects))
    }

    fun broadcast(vararg objects: Any?) {
        Bukkit.getServer().broadcastMessage(asString(*objects))
    }

    fun asString(vararg objects: Any?): String {
        return format(*objects as Array<out Any>)
    }

    private fun format(vararg objects: Any): String {
        var string = localizationManager!!.localize(message)
        if (showPrefix) {
            string = localizationManager.localize(message) + " " + string
        }
        for (i in objects.indices) {
            val o = objects[i]
            string = string!!.replace("{$i}", o.toString())
        }
        return ChatColor.translateAlternateColorCodes('&', string!!)
    }

    companion object {
        fun getLocalizationManager(): LocalizationManager? {
            return LocalizationManager.getInstance(
                JavaPlugin.getPlugin(
                    FwAuctionHouse::class.java
                )
            )
        }
    }

    init {
        this.message = MessageUtils.rewritePlaceholders(message)
        this.showPrefix = showPrefix
        localizationManager = FwAuctionHouse.localizationManager
    }
}