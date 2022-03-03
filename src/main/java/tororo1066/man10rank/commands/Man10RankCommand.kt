package tororo1066.man10rank.commands

import tororo1066.man10rank.Man10Rank
import tororo1066.tororopluginapi.sCommand.SCommand
import tororo1066.tororopluginapi.sCommand.SCommandArg
import tororo1066.tororopluginapi.sCommand.SCommandObject
import tororo1066.tororopluginapi.sCommand.SCommandOnlyPlayerData
import java.util.function.Consumer

class Man10RankCommand : SCommand("mr") {

    init {
        addCommand(SCommandObject().addArg(SCommandArg().addAllowString("check")).setExecutor(
                Consumer<SCommandOnlyPlayerData> { Man10Rank.userData[it.sender.uniqueId]?.showNextRank() }
        ))
    }
}