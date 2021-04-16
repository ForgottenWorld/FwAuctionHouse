package me.kaotich00.fwauctionhouse.commands.admin

import com.google.inject.Inject
import me.kaotich00.fwauctionhouse.commands.api.AdminCommandHandler
import me.kaotich00.fwauctionhouse.services.MarketAreaService
import org.bukkit.entity.Player

sealed class PosCommandHandler(
    positionNumber: Int,
    private val positionSetter: (Player) -> Unit
) : AdminCommandHandler(
    name = "pos$positionNumber",
    requiredArgs = 0,
    usage = "/market pos$positionNumber",
) {

    override fun onAdminOnlyCommand(sender: Player, args: Array<String>) {
        positionSetter.invoke(sender)
    }

    class One @Inject constructor(
        marketAreaService: MarketAreaService
    ) : PosCommandHandler(
        positionNumber = 1,
        marketAreaService::buildPos1
    )

    class Two @Inject constructor(
        marketAreaService: MarketAreaService
    ) : PosCommandHandler(
        positionNumber = 2,
        marketAreaService::buildPos2
    )
}