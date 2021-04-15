package me.kaotich00.fwauctionhouse.message

import me.kaotich00.fwauctionhouse.locale.LocalizationManager
import net.kyori.adventure.audience.Audience
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.TextComponent
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.command.CommandSender

enum class Message(
    private val showPrefix: Boolean = true
) {
    HELP_MESSAGE(false),
    SOLD_ITEM,
    BOUGHT_ITEM,
    INVENTORY_FULL,
    NOT_ENOUGH_MONEY,
    CANNOT_SELL_AIR,
    PURCHASE_MESSAGE(false),
    VALIDATED_TOKEN,
    VALIDATED_TOKEN_MESSAGE,
    DECLINED,
    ENTER_BUILD_MODE,
    ALREADY_IN_BUILD_MODE,
    NOT_IN_BUILD_MODE,
    EXIT_BUILD_MODE,
    FIRST_POSITION_SET,
    SECOND_POSITION_SET,
    MARKET_AREA_CREATED,
    MARKET_AREA_DELETED,
    NO_SUCH_MARKET_AREA,
    POSSIBLE_OPTIONS_ENTER_EXIT;


    fun send(audience: Audience, vararg params: Any) {
        audience.sendMessage(asComponent(*params))
    }

    fun asComponent(vararg params: Any) = format(params)

    private fun format(params: Array<out Any>): Component {
        val localized = requireLocalizationManager().localize(name, params)
        return if (showPrefix) {
            PREFIX.append(localized)
        } else {
            localized
        }
    }

    private fun requireLocalizationManager() =
        localizationManager ?: error("LocalizationManager not set")

    companion object {

        private val PREFIX = TextComponent.ofChildren(
            Component.text("[", NamedTextColor.DARK_GRAY),
            Component.text("Fw", NamedTextColor.GRAY),
            Component.text("Market", NamedTextColor.GREEN),
            Component.text("] ", NamedTextColor.DARK_GRAY)
        )

        var localizationManager: LocalizationManager? = null
    }
}