package tororo1066.man10rank.listeners

import org.bukkit.event.player.PlayerJoinEvent
import tororo1066.man10rank.Man10Rank
import tororo1066.man10rank.data.PlayerData
import tororo1066.man10rank.playTime.PlayTimeCounter
import tororo1066.tororopluginapi.sEvent.SEventInterface

class PlayerJoinListener : SEventInterface<PlayerJoinEvent>(Man10Rank.plugin,PlayerJoinEvent::class.java) {
    override fun executeEvent(e: PlayerJoinEvent) {
        var data = PlayerData.fromDB(e.player.uniqueId)
        if (data == null){
            data = PlayerData.createData(e.player)
        }

        Man10Rank.userData[e.player.uniqueId] = data

    }
}