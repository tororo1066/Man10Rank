package tororo1066.man10rank.listeners

import org.bukkit.event.player.PlayerJoinEvent
import tororo1066.man10rank.Man10Rank
import tororo1066.man10rank.data.PlayerData
import tororo1066.tororopluginapi.sEvent.SEventInterface

class PlayerJoinListener : SEventInterface<PlayerJoinEvent>(Man10Rank.plugin,PlayerJoinEvent::class.java) {
    override fun executeEvent(e: PlayerJoinEvent) {
        if (Man10Rank.userData.containsKey(e.player.uniqueId))return
        PlayerData.createData(e.player)
    }
}