package tororo1066.man10rank

import me.staartvin.statz.Statz
import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin
import tororo1066.man10rank.commands.Man10RankCommand
import tororo1066.man10rank.data.PlayerData
import tororo1066.man10rank.data.RankData
import tororo1066.man10rank.listeners.PlayerJoinListener
import tororo1066.man10rank.listeners.PlayerQuitListener
import tororo1066.man10rank.pathRequest.AbstractPathRequest
import tororo1066.man10rank.pathRequest.requests.BlockBreakRequest
import tororo1066.man10rank.pathRequest.requests.EconomyRequest
import tororo1066.man10rank.pathRequest.requests.PermissionRequest
import tororo1066.man10rank.pathRequest.requests.PlayTimeRequest
import tororo1066.man10rank.playTime.PlayTimeCounter
import tororo1066.tororopluginapi.SMySQL
import tororo1066.tororopluginapi.otherPlugin.SVault
import java.io.File
import java.util.*

class Man10Rank : JavaPlugin() {

    companion object{
        lateinit var mysql : SMySQL
        lateinit var plugin: Man10Rank
        lateinit var vault: SVault
        var statz: Statz? = null
        val requestList = HashMap<String,AbstractPathRequest>()
        lateinit var parent: RankData
        val rankList = HashMap<String,RankData>()
        val userData = HashMap<UUID,PlayerData>()
        const val prefix = "§b§l[§d§lMan10§6§lRank§b§l]§r "
        fun withPrefix(s : String): String {
            return prefix + s
        }
    }

    override fun onEnable() {
        saveDefaultConfig()
        plugin = this
        mysql = SMySQL(this)
        vault = SVault()
        createTable()
        val statz = Bukkit.getPluginManager().getPlugin("Statz")
        if (statz == null || !statz.isEnabled){
            logger.warning("Statzが導入されていません")
        } else {
            Man10Rank.statz = statz as Statz
        }
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
        Man10RankCommand()
        PlayerJoinListener()
        PlayerQuitListener()

        PlayTimeCounter()
    }

    fun registerRequests(){
        add(PermissionRequest())
        add(PlayTimeRequest())
        add(BlockBreakRequest())
        add(EconomyRequest())
    }

    fun add(request: AbstractPathRequest){
        requestList[request.configString] = request
    }

    private fun createTable(){
        mysql.execute("CREATE TABLE IF NOT EXISTS `user_data` (\n" +
                "    `id` INT(10) NOT NULL AUTO_INCREMENT,\n" +
                "    `name` VARCHAR(16) NOT NULL COLLATE 'utf8mb4_0900_ai_ci',\n" +
                "    `uuid` VARCHAR(36) NOT NULL COLLATE 'utf8mb4_0900_ai_ci',\n" +
                "    `nowRank` TEXT NOT NULL COLLATE 'utf8mb4_0900_ai_ci',\n" +
                "    `time` BIGINT(19) NULL DEFAULT NULL,\n" +
                "    PRIMARY KEY (`id`) USING BTREE\n" +
                ");")
    }
}