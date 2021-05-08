package me.kaotich00.fwauctionhouse.message

import net.kyori.adventure.audience.Audience
import net.kyori.adventure.text.Component

fun Component.send(audience: Audience) = audience.sendMessage(this)