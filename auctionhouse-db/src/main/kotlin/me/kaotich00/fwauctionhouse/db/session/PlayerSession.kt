package me.kaotich00.fwauctionhouse.db.session

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID

class PlayerSession(id: EntityID<Int>): IntEntity(id) {

    companion object : IntEntityClass<PlayerSession>(PlayerSessions)

    
    var username by PlayerSessions.username

    var token by PlayerSessions.token

    var expiration by PlayerSessions.expiration

    var isValidated by PlayerSessions.isValidated
}