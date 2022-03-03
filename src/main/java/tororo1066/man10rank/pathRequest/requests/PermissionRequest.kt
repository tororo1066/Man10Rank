package tororo1066.man10rank.pathRequest.requests

import org.bukkit.configuration.ConfigurationSection
import org.bukkit.entity.Player
import tororo1066.man10rank.pathRequest.AbstractPathRequest

class PermissionRequest : AbstractPathRequest("permission") {
    override fun isSuccess(p: Player, config: ConfigurationSection): Boolean {
        return p.hasPermission(config.getString("perm")?:"")
    }
}