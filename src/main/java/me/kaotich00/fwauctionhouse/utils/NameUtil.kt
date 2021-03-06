package me.kaotich00.fwauctionhouse.utils

object NameUtil {

    fun filterByStart(list: List<String>?, startingWith: String?): List<String> {
        if (list == null || startingWith == null) return listOf()
        return list.filter{ it.startsWith(startingWith, ignoreCase = true) }
    }

}