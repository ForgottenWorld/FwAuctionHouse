package me.kaotich00.fwauctionhouse.utils

import org.bukkit.ChatColor

object MessageUtils {

    var EOL = "\n"

    val pluginPrefix: String
        get() = ChatColor.DARK_GRAY.toString() + "[" +
                ChatColor.YELLOW + "Fw" +
                ChatColor.GOLD + ChatColor.BOLD + "War" +
                ChatColor.DARK_GRAY + "]"

    val chatHeader: String
        get() {
            return ChatColor.YELLOW.toString() + "oOo--------------------[ " +
                    ChatColor.YELLOW + "Fw" +
                    ChatColor.GOLD + ChatColor.BOLD + "War" +
                    ChatColor.YELLOW + " ]-------------------oOo "
        }

    fun formatSuccessMessage(message: String): String = ChatColor.GREEN.toString() + message

    fun formatErrorMessage(message: String) = ChatColor.RED.toString() + message

    fun rewritePlaceholders(input: String): String {
        var inputModified = input
        var i = 0
        while (inputModified.contains("{}")) {
            inputModified = inputModified.replaceFirst("{}", "{" + i++ + "}")
        }
        return inputModified
    }
}