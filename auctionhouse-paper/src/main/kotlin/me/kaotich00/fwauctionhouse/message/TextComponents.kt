package me.kaotich00.fwauctionhouse.message

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.JoinConfiguration
import net.kyori.adventure.text.event.ClickEvent
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.inventory.ItemStack

object TextComponents {

    private val greenSeparator = Component
        .text("${"-".repeat(42)}\n", NamedTextColor.GREEN)
        .decorate(TextDecoration.BOLD)
        .decorate(TextDecoration.STRIKETHROUGH)

    val newLine = Component.text("\n")


    private fun acceptPurchase(listingId: Int) = Component
        .text("[CLICK HERE TO CONFIRM]")
        .color(NamedTextColor.GREEN)
        .clickEvent(ClickEvent.runCommand("/market confirm $listingId"))
        .hoverEvent(
            Component.text("Click to accept the purchase")
                .color(NamedTextColor.GREEN)
                .decorate(TextDecoration.ITALIC)
                .asHoverEvent()
        )

    private fun declinePurchase(listingId: Int) = Component
        .text("[CLICK HERE TO DECLINE]\n")
        .color(NamedTextColor.RED)
        .clickEvent(ClickEvent.runCommand("/market decline $listingId"))
        .hoverEvent(
            Component.text("Click to decline the purchase")
                .color(NamedTextColor.RED)
                .decorate(TextDecoration.ITALIC)
                .asHoverEvent()
        )

    fun purchaseConfirmation(
        listingId: Int,
        itemStack: ItemStack
    ) = Component.join(
        JoinConfiguration.noSeparators(),
        greenSeparator,
        newLine,
        Message.PURCHASE_MESSAGE.asComponent(
            itemStack.i18NDisplayName ?: "N/D", 
            itemStack.amount
        ),
        acceptPurchase(listingId),
        Component.text(" "),
        declinePurchase(listingId),
        newLine,
        greenSeparator,
    )
    
    private fun confirmIdentity(playerSessionId: Int) = Component
        .text("[CLICK HERE TO CONFIRM YOUR IDENTITY]\n", NamedTextColor.GREEN)
        .clickEvent(ClickEvent.runCommand("/market validateToken $playerSessionId"))
        .hoverEvent(
            Component.text("Click to validate your identity", NamedTextColor.GREEN)
                .decorate(TextDecoration.ITALIC)
                .asHoverEvent()
        )

    fun tokenConfirmation(playerSessionId: Int) = Component.join(
        JoinConfiguration.noSeparators(),
        greenSeparator,
        newLine,
        Message.VALIDATE_TOKEN_MESSAGE.asComponent(),
        confirmIdentity(playerSessionId),
        newLine,
        greenSeparator,
    )
}