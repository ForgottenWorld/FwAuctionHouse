package me.kaotich00.fwauctionhouse.services

import me.kaotich00.fwauctionhouse.model.PendingSell
import me.kaotich00.fwauctionhouse.model.PendingToken

interface ListingsService {

    fun scheduleSellingTask()

    fun scheduleConfirmTokenTask()

    fun removeFromPendingSells(pendingSell: PendingSell)

    fun getPendingSell(id: Int): PendingSell?

    fun removeFromPendingToken(pendingToken: PendingToken)

    fun getPendingToken(id: Int): PendingToken?
}