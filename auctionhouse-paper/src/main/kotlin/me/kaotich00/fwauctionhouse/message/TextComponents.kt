package me.kaotich00.fwauctionhouse.message

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration

object TextComponents {

    val greenSeparator = Component
        .text("${"-".repeat(42)}\n", NamedTextColor.GREEN)
        .decorate(TextDecoration.BOLD)
        .decorate(TextDecoration.STRIKETHROUGH)

    val newLine = Component.text("\n")
}