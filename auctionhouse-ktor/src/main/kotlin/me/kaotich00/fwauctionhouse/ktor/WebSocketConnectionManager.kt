package me.kaotich00.fwauctionhouse.ktor

import io.ktor.http.cio.websocket.*
import me.kaotich00.fwauctionhouse.db.session.PlayerSession
import me.kaotich00.fwauctionhouse.db.session.PlayerSessions
import org.jetbrains.exposed.sql.transactions.transaction
import java.lang.Exception

object WebSocketConnectionManager {

    private val sessionsByToken = mutableMapOf<String, Connection>()


    suspend fun registerConnection(token: String, connection: Connection) {
        sessionsByToken[token] = connection
        val playerSession = transaction {
            PlayerSession.find { PlayerSessions.token eq token }.firstOrNull()
        } ?: return
        if (playerSession.isValidated == true) {
            connection.session.send(TOKEN_CONFIRMED)
        }
    }

    fun unregisterConnection(token: String) {
        sessionsByToken.remove(token)
    }


    suspend fun onListingsChanged() {
        sessionsByToken.values.forEach {
            try {
                it.session.send(LISTINGS_CHANGED)
            } catch (e: Exception) {
                println("$it has disconnected, removing it!")
                unregisterConnection(it.token)
            }
        }
    }

    suspend fun onTokenConfirmed(token: String) {
        val conn = sessionsByToken[token] ?: return
        try {
            conn.session.send(TOKEN_CONFIRMED)
        } catch (e: Exception) {
            println("$conn has disconnected, removing it!")
            unregisterConnection(token)
        }
    }


    private const val LISTINGS_CHANGED = "LISTINGS_CHANGED"

    private const val TOKEN_CONFIRMED = "TOKEN_CONFIRMED"
}