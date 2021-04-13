package me.kaotich00.fwauctionhouse.model

data class PendingToken(
    var sessionId: Int,
    var username: String,
    var token: String
)