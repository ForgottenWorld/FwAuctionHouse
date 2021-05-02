package me.kaotich00.fwauctionhouse.commands.user

import me.kaotich00.fwauctionhouse.commands.api.PlayerCommandHandler
import me.kaotich00.fwauctionhouse.db.session.PlayerSessions
import me.kaotich00.fwauctionhouse.message.Message
import org.bukkit.entity.Player
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.transactions.transaction

class LogoutCommandHandler : PlayerCommandHandler(
    name = "logout",
    requiredArgs = 0,
    usage = "/market logout",
    info = "Invalidates all web sessions bound to the calling player"
) {

    override fun onCommand(sender: Player, args: Array<String>) {
        val deletedSessionsAmount = transaction {
            PlayerSessions.deleteWhere { PlayerSessions.username eq sender.name }
        }

        if (deletedSessionsAmount > 0) {
            Message.YOU_WERE_LOGGED_OUT
        } else {
            Message.NO_OPEN_SESSIONS
        }.send(sender)
    }

}