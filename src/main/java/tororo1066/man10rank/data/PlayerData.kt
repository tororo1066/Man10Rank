package tororo1066.man10rank.data

import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.OfflinePlayer
import org.bukkit.Server
import org.bukkit.entity.Player
import tororo1066.man10rank.Man10Rank
import tororo1066.man10rank.Man10Rank.Companion.withPrefix
import java.util.UUID

class PlayerData {

    companion object{
        fun fromDB(): ArrayList<PlayerData> {
            val rs = Man10Rank.mysql.asyncQuery("select * from user_data")
            val list = ArrayList<PlayerData>()
            for (result in rs){
                val data = PlayerData()
                data.uuid = UUID.fromString(result.getString("uuid"))
                data.mcid = result.getString("name")
                data.loginTime = result.getLong("time")
                data.nowRank = Man10Rank.rankList[result.getString("nowRank")]?:continue
                val rank = Man10Rank.rankList[result.getString("nowRank")]!!
                for (child in rank.children){
                    data.nextData.add(Man10Rank.rankList[child]?:continue)
                }
                list.add(data)
            }
            return list
        }

        fun createData(p: OfflinePlayer): Boolean {
            if (Man10Rank.userData.containsKey(p.uniqueId))return false
            Man10Rank.mysql.asyncExecute("insert into user_data (name, uuid, nowRank, time) values ('${p.name}', '${p.uniqueId}', '${Man10Rank.parent.includeName}', 0)")
            val data = PlayerData()
            data.uuid = p.uniqueId
            data.mcid = p.name.toString()
            data.nowRank = Man10Rank.parent
            Man10Rank.parent.onSuccess.forEach {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(),it.replace("<player>",p.name.toString()))
            }
            for (child in Man10Rank.parent.children){
                data.nextData.add(Man10Rank.rankList[child]?:continue)
            }
            Man10Rank.userData[p.uniqueId] = data
            return true
        }
    }

    lateinit var uuid: UUID
    var mcid = ""

    lateinit var nowRank : RankData

    val nextData = ArrayList<RankData>()

    var loginTime: Long = 0

    fun getLoginString(): String {
        var minutes = loginTime.toInt()
        var hours = 0
        var days = 0
        if (minutes >= 60){
            hours = (minutes / 60)
            minutes -= hours * 60
        }
        if (hours >= 24){
            days = (hours / 24)
            hours -= days * 24
        }

        return "${if (days == 0) "" else "${days}日"}${if (hours == 0) "" else "${hours}時間"}${if (minutes == 0) "" else "${minutes}分"}"
    }

    fun showNextRank(){
        val p = Bukkit.getPlayer(uuid)?:return
        if (p.name != mcid){
            Man10Rank.mysql.asyncExecute("update user_data set name = '${p.name}' where uuid = '${uuid}'")
        }

        p.sendMessage(withPrefix("§e§lログイン時間：§b${getLoginString()}"))

        if (nextData.isEmpty()){
            p.sendMessage(withPrefix("§4次のランクはありません"))
            return
        }

        for (data in nextData){
            p.sendMessage(withPrefix(data.pathName))
            for (i in data.paths.indices){
                val isSuccess = data.paths[i].isSuccess(p)
                if (isSuccess){
                    p.sendMessage(withPrefix("§a${data.pathMessages[i]}"))
                }else{
                    p.sendMessage(withPrefix("§c${data.pathMessages[i]}"))
                }
            }

            if (data.isSuccess(p)){
                rankUp(data)
                return
            }
        }
    }

    fun rankUp(rankData: RankData){
        nowRank = rankData
        Man10Rank.mysql.asyncExecute("update user_data set nowRank = '${rankData.includeName}' where uuid = '${uuid}'")
        nextData.clear()
        for (child in rankData.children){
            nextData.add(Man10Rank.rankList[child]?:continue)
        }

        nowRank.onSuccess.forEach {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(),it.replace("<player>",mcid))
        }

        Bukkit.broadcast(Component.text(withPrefix("§f§l${mcid}§dが§r${rankData.name}§dにランクアップしました！")),Server.BROADCAST_CHANNEL_USERS)

    }


}