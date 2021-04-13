package me.kaotich00.fwauctionhouse.utils

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor

object MessageUtils {

    fun formatSuccessMessage(message: String) = Component.text(message, NamedTextColor.GREEN)

    fun formatErrorMessage(message: String) = Component.text(message, NamedTextColor.RED)
}