package me.kaotich00.fwauctionhouse.message

import me.kaotich00.fwauctionhouse.locale.LocalizationManager
import net.kyori.adventure.audience.Audience
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.JoinConfiguration
import net.kyori.adventure.text.format.NamedTextColor

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
    VALIDATE_TOKEN_MESSAGE,
    DECLINED,
    ENTER_BUILD_MODE,
    ALREADY_IN_BUILD_MODE,
    NOT_IN_BUILD_MODE,
    EXIT_BUILD_MODE,
    FIRST_POSITION_SET,
    SECOND_POSITION_SET,
    MARKET_AREA_CREATED,
    MARKET_AREA_REMOVED,
    NO_SUCH_MARKET_AREA,
    YOU_WERE_LOGGED_OUT,
    NO_OPEN_SESSIONS,
    POSSIBLE_OPTIONS_ENTER_EXIT,
    POSSIBLE_OPTIONS_ID_THIS,
    INSERT_VALID_AMOUNT,
    YOU_MAY_NOT_ACCESS_MARKET_HERE,
    INSERT_VALID_SESSION_ID,
    INSERT_VALID_ID,
    YOU_DONT_HAVE_PERMISSION_TO_USE_COMMAND,
    ONLY_PLAYERS_CAN_RUN_THIS_COMMAND,
    NOT_ENOUGH_ARGUMENTS;


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

        private val PREFIX = Component.join(
            JoinConfiguration.noSeparators(),
            Component.text("[", NamedTextColor.DARK_GRAY),
            Component.text("Fw", NamedTextColor.GRAY),
            Component.text("Market", NamedTextColor.GREEN),
            Component.text("] ", NamedTextColor.DARK_GRAY)
        )

        var localizationManager: LocalizationManager? = null
    }
}