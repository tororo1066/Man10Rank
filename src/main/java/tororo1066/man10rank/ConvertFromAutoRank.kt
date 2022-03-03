package tororo1066.man10rank

import org.bukkit.Bukkit
import org.bukkit.configuration.file.YamlConfiguration
import tororo1066.man10rank.data.PlayerData
import tororo1066.man10rank.playTime.PlayTimeCounter
import java.io.File
import java.util.UUID

class ConvertFromAutoRank {

    companion object{
        fun convert(): Boolean {
            val file = File((Bukkit.getPluginManager().getPlugin("AutoRank")?:return false).dataFolder.path + "/data/Total_time.yml")
            if (!file.exists())return false

            Bukkit.getLogger().warning("[Man10Rank] ラグが発生する恐れがあります！")

            val yaml = YamlConfiguration.loadConfiguration(file)

            val mysql = Man10Rank.mysql
            for (uuidString in yaml.getKeys(false)){
                val data = yaml.getInt(uuidString)
                val uuid = UUID.fromString(uuidString)
                if (Man10Rank.userData.containsKey(uuid)){
                    Man10Rank.userData[uuid]!!.loginTime = data.toLong()
                    mysql.asyncExecute("update user_data set time = $data where uuid = '${uuid}'")
                } else {
                    PlayerData.createData(Bukkit.getOfflinePlayer(uuid))
                    Man10Rank.userData[uuid]!!.loginTime = data.toLong()
                    mysql.asyncExecute("update user_data set time = $data where uuid = '${uuid}'")
                }
                if (PlayTimeCounter.countPlayers.containsKey(uuid)){
                    PlayTimeCounter.countPlayers[uuid]!!.time = data.toLong()
                }
            }
            Bukkit.getLogger().info("[Man10Rank] §a完了しました")
            return true
        }
    }
}