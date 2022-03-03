package tororo1066.man10rank.pathRequest.requests

import org.bukkit.configuration.ConfigurationSection
import org.bukkit.entity.Player
import tororo1066.man10rank.Man10Rank
import tororo1066.man10rank.pathRequest.AbstractPathRequest

class PlayTimeRequest : AbstractPathRequest("playtime") {
    override fun isSuccess(p: Player, config: ConfigurationSection): Boolean {
        val user = Man10Rank.userData[p.uniqueId]?:return false
        return user.loginTime >= config.getLong("time")
    }

}