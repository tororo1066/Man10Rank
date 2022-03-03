package tororo1066.man10rank.data

import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.entity.Player
import tororo1066.man10rank.Man10Rank
import tororo1066.man10rank.pathRequest.AbstractPathRequest
import java.io.File

class RankData {

    companion object{
        fun loadFromYml(id : String): RankData {
            val file = File(Man10Rank.plugin.dataFolder.path + "/ranks/${id}.yml")
            if (!file.exists()) return RankData()
            val yaml = YamlConfiguration.loadConfiguration(file)
            val data = RankData()
            data.includeName = id
            data.name = yaml.getString("name")?:"Null"
            data.pathName = yaml.getString("pathName")?:"Null"

            data.children.addAll(yaml.getStringList("children"))
            val paths = (yaml.getConfigurationSection("paths")?:return RankData())
            for (path in paths.getKeys(false)){
                if (!Man10Rank.requestList.containsKey(path))continue
                val pathRequest = Man10Rank.requestList[path]!!
                pathRequest.config = paths.getConfigurationSection(path)?:return RankData()
                data.paths.add(pathRequest)
            }

            for (message in yaml.getStringList("pathMessages")){
                data.pathMessages.add(message)
            }
            data.onSuccess.addAll(yaml.getStringList("onSuccess"))

            if (yaml.getString("parent") == null){
                Man10Rank.parent = data
            }else{
                data.parent = yaml.getString("parent")!!
            }

            data.loaded = true


            return data
        }


    }

    var includeName = ""
    var name = ""
    var pathName = ""
    val paths = ArrayList<AbstractPathRequest>()
    val pathMessages = ArrayList<String>()
    val onSuccess = ArrayList<String>()
    var parent = ""
    val children = ArrayList<String>()
    var loaded = false

    fun isSuccess(p : Player): Boolean {
        for (path in paths){
            if (!path.isSuccess(p))return false
        }
        return true
    }

}