package tororo1066.man10rank.listeners

import org.bukkit.event.player.PlayerQuitEvent
import tororo1066.man10rank.Man10Rank
import tororo1066.tororopluginapi.sEvent.SEventInterface

class PlayerQuitListener : SEventInterface<PlayerQuitEvent>(Man10Rank.plugin,PlayerQuitEvent::class.java) {
    override fun executeEvent(e: PlayerQuitEvent) {
        Man10Rank.userData.remove(e.player.uniqueId)
    }
}