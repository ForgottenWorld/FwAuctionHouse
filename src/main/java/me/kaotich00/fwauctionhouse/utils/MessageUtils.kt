package me.kaotich00.fwauctionhouse.utils

import org.bukkit.ChatColor

object MessageUtils {
    var EOL = "\n"
    val pluginPrefix: String
        get() = ChatColor.DARK_GRAY.toString() + "[" +
                ChatColor.YELLOW + "Fw" +
                ChatColor.GOLD + ChatColor.BOLD + "War" +
                ChatColor.DARK_GRAY + "]"

    fun chatHeader(): String {
        return ChatColor.YELLOW.toString() + "oOo--------------------[ " +
                ChatColor.YELLOW + "Fw" +
                ChatColor.GOLD + ChatColor.BOLD + "War" +
                ChatColor.YELLOW + " ]-------------------oOo "
    }

    fun formatSuccessMessage(message: String): String {
        var message = message
        message = ChatColor.GREEN.toString() + message
        return message
    }

    fun formatErrorMessage(message: String?): String {
        var message = message
        message = ChatColor.RED.toString() + message
        return message
    }

    fun helpMessage(): String {
        var message = chatHeader()
        message += """
     
        ${ChatColor.GRAY}>> ${ChatColor.DARK_AQUA}/war ${ChatColor.AQUA}start 
        ${ChatColor.GRAY}>> ${ChatColor.DARK_AQUA}/war ${ChatColor.AQUA}plot 
        """.trimIndent()
        return message
    }

    fun rewritePlaceholders(input: String): String {
        var inputModified = input
        var i = 0
        while (inputModified.contains("{}")) {
            inputModified = inputModified.replaceFirst("{}", "{" + i++ + "}")
        }
        return inputModified
    }

}