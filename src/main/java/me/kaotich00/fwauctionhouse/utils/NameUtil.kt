package me.kaotich00.fwauctionhouse.utils

import java.util.stream.Collectors

object NameUtil {
    fun filterByStart(list: List<String?>?, startingWith: String?): List<String?> {
        return if (list == null || startingWith == null) {
            emptyList<String>()
        } else list.stream().filter { name: String? -> name!!.toLowerCase().startsWith(startingWith.toLowerCase()) }
            .collect(Collectors.toList())
    }
}