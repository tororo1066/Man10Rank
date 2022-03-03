package tororo1066.man10rank.listeners

import org.bukkit.Bukkit
import org.bukkit.event.player.PlayerQuitEvent
import tororo1066.man10rank.Man10Rank
import tororo1066.man10rank.playTime.PlayTimeCounter
import tororo1066.tororopluginapi.sEvent.SEventInterface

class PlayerQuitListener : SEventInterface<PlayerQuitEvent>(Man10Rank.plugin,PlayerQuitEvent::class.java) {
    override fun executeEvent(e: PlayerQuitEvent) {
        if (!PlayTimeCounter.countPlayers.containsKey(e.player.uniqueId))return
        val data = PlayTimeCounter.countPlayers[e.player.uniqueId]!!
        Bukkit.getScheduler().cancelTask(data.taskId)
        Man10Rank.mysql.asyncExecute("update user_data set time = ${data.time} where uuid = ''${data.uuid}")
        PlayTimeCounter.countPlayers.remove(e.player.uniqueId)
    }
}