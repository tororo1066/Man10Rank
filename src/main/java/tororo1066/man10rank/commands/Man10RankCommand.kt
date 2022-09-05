package tororo1066.man10rank.commands

import org.bukkit.command.CommandSender
import tororo1066.man10rank.ConvertFromAutoRank
import tororo1066.man10rank.Man10Rank
import tororo1066.man10rank.Man10Rank.Companion.withPrefix
import tororo1066.tororopluginapi.sCommand.*
import tororo1066.tororopluginapi.utils.DateType
import tororo1066.tororopluginapi.utils.toJPNDateStr
import tororo1066.tororopluginapi.utils.toPlayer
import java.util.function.Consumer

class Man10RankCommand : SCommand("mr") {

    init {
        clearCommands()

        registerReportCommand(Man10Rank.plugin,"mr.user","mr.op")
        setCommandNoFoundEvent { showHelp(it.sender) }

        addCommand(SCommandObject().addNeedPermission("mr.user").addArg(SCommandArg().addAllowString("check"))
            .setPlayerExecutor {
                Man10Rank.userData[it.sender.uniqueId]?.showNextRank()
            })
        addCommand(SCommandObject().addNeedPermission("mr.user")
            .addArg(SCommandArg().addAllowString("top")).addArg(SCommandArg().addAllowType(SCommandArgType.INT).addAlias("ページ"))
            .setNormalExecutor {
                ranking(it.sender,it.args[1].toInt())
            })
        addCommand(SCommandObject().addNeedPermission("mr.user")
            .addArg(SCommandArg().addAllowString("top"))
            .setNormalExecutor {
                ranking(it.sender,1)
            })

        addCommand(SCommandObject().addNeedPermission("mr.op")
            .addArg(SCommandArg().addAllowString("op"))
            .addArg(SCommandArg().addAllowString("convert"))
            .addArg(SCommandArg().addAllowType(SCommandArgType.INT).addAlias("first"))
            .addArg(SCommandArg().addAllowType(SCommandArgType.INT).addAlias("last")).setNormalExecutor {
                ConvertFromAutoRank.convert(it.args[2].toInt()..it.args[3].toInt())
            })

        addCommand(SCommandObject().addNeedPermission("mr.op")
            .addArg(SCommandArg().addAllowString("op"))
            .addArg(SCommandArg().addAllowString("setRank"))
            .addArg(SCommandArg().addAllowType(SCommandArgType.ONLINE_PLAYER))
            .addArg(SCommandArg().addAllowString(Man10Rank.rankList.map { it.key }.toTypedArray()))
            .setNormalExecutor {
                val p = it.args[2].toPlayer()!!
                val rank = Man10Rank.rankList[it.args[3]]!!
                val userData = Man10Rank.userData[p.uniqueId]
                if (userData == null){
                    it.sender.sendMessage(Man10Rank.prefix + "§cユーザーのデータが存在しません")
                    return@setNormalExecutor
                }

                userData.rankUp(rank,false)
                it.sender.sendMessage(Man10Rank.prefix + "§a${p.name}§rを§a${rank.name}§rにしました")
            })
    }

    fun showHelp(sender: CommandSender){
        sender.sendMessage("§6====================§d§lMan10Rank§6====================")
        if (sender.hasPermission("mr.user")){
            sender.sendMessage("§b/mr check §a次のランクの条件を確認します")
            sender.sendMessage("§b/mr top (ページ) §aログイン時間ランキングを表示します")
        }
        if (sender.hasPermission("mr.op")){
            sender.sendMessage("§b/mr op convert §aAutoRankからログイン時間を引き継ぎます")
            sender.sendMessage("§b/mr op setRank <Player> <Rank> §a特定のプレイヤーのランクを設定します")
        }
        sender.sendMessage("§6====================§d§lMan10Rank§6==Author:tororo_1066")
    }

    private fun ranking(p: CommandSender, page: Int) {
        val rs = Man10Rank.mysql.asyncQuery("select * from user_data order by time desc limit 10 offset ${(page*10)-10}")
        val rankingList = HashMap<String,Long>()
        for (result in rs) {
            rankingList[result.getString("name")] = result.getLong("time")
        }

        val sortedMap = rankingList.entries.sortedByDescending { it.value }

        p.sendMessage(withPrefix("§e§lログイン時間ランキング"))
        for ((index, map) in sortedMap.withIndex()) {
            p.sendMessage("§7${(index + 1) * page}§e§l${map.key}§f：§b${map.value.toJPNDateStr(DateType.MINUTE,DateType.YEAR)}")
        }
    }
}