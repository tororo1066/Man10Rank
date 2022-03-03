package tororo1066.man10rank.commands

import org.bukkit.command.CommandSender
import tororo1066.man10rank.Man10Rank
import tororo1066.man10rank.Man10Rank.Companion.withPrefix
import tororo1066.tororopluginapi.sCommand.*
import java.util.function.Consumer

class Man10RankCommand : SCommand("mr") {

    init {
        setCommandNoFoundEvent { showHelp(it.sender) }

        addCommand(SCommandObject().addNeedPermission("mr.user").addArg(SCommandArg().addAllowString("check")).setExecutor(
                Consumer<SCommandOnlyPlayerData> { Man10Rank.userData[it.sender.uniqueId]?.showNextRank() }
        ))
        addCommand(SCommandObject().addNeedPermission("mr.user")
                .addArg(SCommandArg().addAllowString("top")).addArg(SCommandArg().addAllowType(SCommandArgType.INT).addAlias("ページ")).setExecutor(
                Consumer<SCommandData> {
                    val p = it.sender
                    p.sendMessage(withPrefix("§e§lログイン時間ランキング"))
                    val sortedMap = Man10Rank.userData.entries.sortedByDescending { data -> data.value.loginTime }
                    for ((index, map) in sortedMap.withIndex()){
                        if (index > it.args[1].toInt()*10-1)break
                        if (index < it.args[1].toInt()*10-10)continue
                        p.sendMessage("§e§l${map.value.mcid}§f：§b${map.value.getLoginString()}")
                    }
                }
        ))
    }

    fun showHelp(sender: CommandSender){
        sender.sendMessage("§6====================§d§lMan10Rank§6====================")
        sender.sendMessage("§b/mr check §a次のランクの条件を確認します")
        sender.sendMessage("§b/mr top <ページ> §aログイン時間ランキングを表示します")
        sender.sendMessage("§6====================§d§lMan10Rank§6====================")
    }
}