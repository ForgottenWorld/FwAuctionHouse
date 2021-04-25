package me.kaotich00.fwauctionhouse.ktor.session

import io.ktor.application.*
import io.ktor.response.*
import io.ktor.routing.*
import me.kaotich00.fwauctionhouse.db.session.PlayerSession
import me.kaotich00.fwauctionhouse.db.session.PlayerSessions
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.LocalDateTime

fun Route.sessionUpdateTokenRoute() {
    get("/updateToken/{username}/{token}") {
        val username = call.parameters["username"] ?: return@get
        val token = call.parameters["token"] ?: return@get

        val playerSession = transaction {
            val session = PlayerSession
                .find { PlayerSessions.username eq username }
                .singleOrNull()

            if (session == null) {
                PlayerSession.new {
                    this.username = username
                    this.token = token
                    this.expiration = LocalDateTime.now().plusMonths(1)
                    this.isValidated = false
                }
            } else {
                session.token = token
                session.expiration = LocalDateTime.now().plusMonths(1)
                session.isValidated = false
                session
            }
        }

        call.respond(mapOf(
            "username" to playerSession.username,
            "token" to playerSession.token,
            "expiration" to playerSession.expiration,
            "isValidated" to playerSession.isValidated
        ))
    }
}