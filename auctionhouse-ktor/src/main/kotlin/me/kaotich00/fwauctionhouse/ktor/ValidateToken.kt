package me.kaotich00.fwauctionhouse.ktor

import io.ktor.application.*
import io.ktor.response.*
import me.kaotich00.fwauctionhouse.db.session.PlayerSession
import me.kaotich00.fwauctionhouse.db.session.PlayerSessions
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.LocalDateTime

fun validateToken(token: String): Map<String, String> {
    val session = transaction {
        PlayerSession
            .find { PlayerSessions.token eq token }
            .singleOrNull()
    }

    return when {
        session == null -> mapOf(
            "status" to "-1",
            "error" to "No user found for the given token"
        )
        session.isValidated != true -> mapOf(
            "status" to "-2",
            "error" to "The token for the user ${session.username} must be validated before proceeding"
        )
        session.expiration?.isBefore(LocalDateTime.now()) != false -> mapOf(
            "status" to "-3",
            "error" to "The token for the user ${session.username} expired"
        )
        else -> mapOf(
            "status" to "1",
            "error" to "The token is valid for the user ${session.username}"
        )
    }
}

suspend fun validateCall(call: ApplicationCall): Boolean {
    val token = call.request.queryParameters["token"] ?: return false

    val validationResult = validateToken(token)
    if (validationResult["status"] != "1") {
        call.respond(validationResult)
        return false
    }

    return true
}