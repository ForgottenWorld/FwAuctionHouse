package me.kaotich00.fwauctionhouse.model.session

import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.`java-time`.datetime

object PlayerSessions : IntIdTable("player_session") {

    val username = varchar("username", 36)

    val token = varchar("token", 255)

    val expiration = datetime("expiration").nullable()

    val isValidated = bool("is_validated").nullable()
}