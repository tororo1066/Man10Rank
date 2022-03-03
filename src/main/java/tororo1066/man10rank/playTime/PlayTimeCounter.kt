package tororo1066.man10rank.playTime

import org.bukkit.Bukkit
import tororo1066.man10rank.Man10Rank
import java.util.UUID

class PlayTimeCounter(private val uuid: UUID) {

    class CountData {
        lateinit var uuid: UUID
        var time : Long = 0
        var taskId = 0
    }

    companion object{
        val countPlayers = HashMap<UUID,CountData>()
    }

    init {
        init()
    }

    private fun init(){
        if (countPlayers.containsKey(uuid))return
        if (!Man10Rank.userData.containsKey(uuid))return
        val user = Man10Rank.userData[uuid]!!

        val data = CountData()

        data.uuid = this.uuid
        data.time = user.loginTime
        data.taskId = Bukkit.getScheduler().runTaskTimer(Man10Rank.plugin, Runnable {
            data.time += 5
            user.loginTime += 5
        },6000,6000).taskId

        countPlayers[uuid] = data
    }


}