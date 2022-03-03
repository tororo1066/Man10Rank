package tororo1066.man10rank

import org.bukkit.Bukkit
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.plugin.java.JavaPlugin
import org.reflections.Reflections
import org.reflections.scanners.SubTypesScanner
import tororo1066.man10rank.commands.Man10RankCommand
import tororo1066.man10rank.data.PlayerData
import tororo1066.man10rank.data.RankData
import tororo1066.man10rank.listeners.PlayerJoinListener
import tororo1066.man10rank.listeners.PlayerQuitListener
import tororo1066.man10rank.pathRequest.AbstractPathRequest
import tororo1066.man10rank.pathRequest.requests.PermissionRequest
import tororo1066.man10rank.pathRequest.requests.PlayTimeRequest
import tororo1066.tororopluginapi.SMySQL
import java.io.File
import java.util.UUID
import java.util.stream.Collectors

class Man10Rank : JavaPlugin() {

    companion object{
        lateinit var mysql : SMySQL
        lateinit var plugin: Man10Rank
        val requestList = HashMap<String,AbstractPathRequest>()
        lateinit var parent: RankData
        val rankList = HashMap<String,RankData>()
        val userData = HashMap<UUID,PlayerData>()
    }

    override fun onEnable() {
        saveDefaultConfig()
        plugin = this
        mysql = SMySQL(this)
        registerRequests()
        val directory = File(dataFolder.path + "/ranks")
        if (!directory.exists()) directory.mkdir()
        if (directory.listFiles() != null){
            for (file in directory.listFiles()!!){
                if (file.extension != "yml")continue
                val data = RankData.loadFromYml(file.nameWithoutExtension)
                if (!data.loaded) {
                    continue
                }
                rankList[file.nameWithoutExtension] = data
            }
        }

        PlayerData.fromDB().forEach {
            userData[it.uuid] = it
        }
        Man10RankCommand()
        PlayerJoinListener()
        PlayerQuitListener()
    }

    fun registerRequests(){
        add(PermissionRequest())
        add(PlayTimeRequest())
    }

    fun add(request: AbstractPathRequest){
        requestList[request.configString] = request
    }
}