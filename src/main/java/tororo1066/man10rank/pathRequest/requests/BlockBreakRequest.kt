package tororo1066.man10rank.pathRequest.requests

import me.staartvin.statz.datamanager.player.PlayerStat
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.entity.Player
import tororo1066.man10rank.Man10Rank
import tororo1066.man10rank.pathRequest.AbstractPathRequest

class BlockBreakRequest : AbstractPathRequest("blockbreak") {
    override fun isSuccess(p: Player, config: ConfigurationSection): Boolean {
        if (Man10Rank.statz == null)return false
        return config.getInt("amount") <= Man10Rank.statz!!.statzAPI.getTotalOf(PlayerStat.BLOCKS_BROKEN,p.uniqueId,null)
    }
}