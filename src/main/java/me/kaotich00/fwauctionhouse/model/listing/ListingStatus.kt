package me.kaotich00.fwauctionhouse.model.listing

enum class ListingStatus(val value: Int) {
    ORDER_AVAILABLE(1),
    ORDER_PLACED(2),
    NO_USER_FOUND(-1),
    NOT_ENOUGH_MONEY(-2),
}