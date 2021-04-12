package me.kaotich00.fwauctionhouse.utils

import com.okkero.skedule.BukkitDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import me.kaotich00.fwauctionhouse.FwAuctionHouse
import kotlin.coroutines.CoroutineContext

fun launch(f: suspend CoroutineScope.() -> Unit) = CoroutineScope(BukkitDispatchers.minecraft).launch(block = f)

fun launchAsync(f: suspend CoroutineScope.() -> Unit) = CoroutineScope(BukkitDispatchers.async).launch(block = f)

object BukkitDispatchers {
    val minecraft: CoroutineContext by lazy {
        BukkitDispatcher(FwAuctionHouse.instance)
    }

    val async: CoroutineContext by lazy {
        BukkitDispatcher(FwAuctionHouse.instance)
    }
}