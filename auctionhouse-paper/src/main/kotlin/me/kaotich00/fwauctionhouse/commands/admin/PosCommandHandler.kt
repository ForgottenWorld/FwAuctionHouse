package me.kaotich00.fwauctionhouse.commands.admin

import com.google.inject.Inject
import me.kaotich00.fwauctionhouse.commands.api.AdminCommandHandler
import me.kaotich00.fwauctionhouse.services.MarketAreaService
import org.bukkit.entity.Player

sealed class PosCommandHandler(
    positionNumber: Int,
    info: String,
    private val positionSetter: (Player) -> Unit
) : AdminCommandHandler(
    name = "pos$positionNumber",
    requiredArgs = 0,
    usage = "/market pos$positionNumber (aiming at block)",
    info = info
) {

    override fun onAdminOnlyCommand(sender: Player, args: Array<String>) {
        positionSetter.invoke(sender)
    }

    class One @Inject constructor(
        marketAreaService: MarketAreaService
    ) : PosCommandHandler(
        positionNumber = 1,
        info = "Select the first position for creating a market area",
        marketAreaService::buildPos1
    )

    class Two @Inject constructor(
        marketAreaService: MarketAreaService
    ) : PosCommandHandler(
        positionNumber = 2,
        info = "Select the second position for creating a market area",
        marketAreaService::buildPos2
    )
}