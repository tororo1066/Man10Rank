package tororo1066.man10rank.data

import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.OfflinePlayer
import org.bukkit.Server
import org.bukkit.entity.Player
import tororo1066.man10rank.Man10Rank
import tororo1066.man10rank.Man10Rank.Companion.withPrefix
import tororo1066.tororopluginapi.utils.DateType
import tororo1066.tororopluginapi.utils.toJPNDateStr
import tororo1066.tororopluginapi.utils.toPlayer
import java.util.UUID

class PlayerData {

    companion object{
        fun fromDB(uuid: UUID): PlayerData? {
            val rs = Man10Rank.mysql.asyncQuery("select * from user_data where uuid = '${uuid}'")
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
                return data
            }
            return null
        }

        fun createData(p: OfflinePlayer): PlayerData {
            if (Man10Rank.userData.containsKey(p.uniqueId))return Man10Rank.userData[p.uniqueId]!!
            if (!Man10Rank.mysql.asyncExecute("insert into user_data (name, uuid, nowRank, time) values ('${p.name}', '${p.uniqueId}', '${Man10Rank.parent.includeName}', 0)"))throw NullPointerException("Please Connect DB.")
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
            return data
        }
    }

    lateinit var uuid: UUID
    var mcid = ""

    lateinit var nowRank : RankData

    val nextData = ArrayList<RankData>()

    var loginTime: Long = 0

    fun showNextRank(){
        val p = Bukkit.getPlayer(uuid)?:return
        if (p.name != mcid){
            Man10Rank.mysql.asyncExecute("update user_data set name = '${p.name}' where uuid = '${uuid}'")
        }

        p.sendMessage(withPrefix("§e§lログイン時間：§b${loginTime.toJPNDateStr(DateType.MINUTE,DateType.YEAR)}"))

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

    fun checkRankUp(){
        val p = uuid.toPlayer()?:return
        for (data in nextData){
            if (data.isSuccess(p)){
                rankUp(data)
                return
            }
        }
    }

    fun rankUp(rankData: RankData, broadcast: Boolean){
        nowRank = rankData
        Man10Rank.mysql.asyncExecute("update user_data set nowRank = '${rankData.includeName}' where uuid = '${uuid}'")
        nextData.clear()
        for (child in rankData.children){
            nextData.add(Man10Rank.rankList[child]?:continue)
        }

        nowRank.onSuccess.forEach {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(),it.replace("<player>",mcid))
        }

        if (broadcast){
            Bukkit.broadcast(Component.text(withPrefix("§f§l${mcid}§dが§r${rankData.name}§dにランクアップしました！")),Server.BROADCAST_CHANNEL_USERS)
        }
    }

    fun rankUp(rankData: RankData){
        rankUp(rankData,true)
    }


}