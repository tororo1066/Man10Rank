package tororo1066.man10rank.pathRequest.requests

import org.bukkit.configuration.ConfigurationSection
import org.bukkit.entity.Player
import tororo1066.man10rank.Man10Rank
import tororo1066.man10rank.pathRequest.AbstractPathRequest

class EconomyRequest : AbstractPathRequest("economy") {
    override fun isSuccess(p: Player, config: ConfigurationSection): Boolean {
        return config.getDouble("amount") <= Man10Rank.vault.getBalance(p.uniqueId)
    }
}