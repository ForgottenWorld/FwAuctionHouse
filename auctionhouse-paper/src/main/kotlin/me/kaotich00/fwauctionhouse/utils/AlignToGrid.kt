package me.kaotich00.fwauctionhouse.utils

fun Int.alignToGrid() = when {
    this == 0 || this % 16 == 0 -> this
    this < 0 -> this - 16 + -this % 16
    this > 0 -> this - this % 16
    else -> throw Exception("Maths is a social construct")
}