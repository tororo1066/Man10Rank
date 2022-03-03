package tororo1066.man10rank.pathRequest

import org.bukkit.configuration.ConfigurationSection
import org.bukkit.entity.Player

abstract class AbstractPathRequest(val configString: String) {

    lateinit var config: ConfigurationSection

    fun isSuccess(p: Player): Boolean {
        return isSuccess(p,config)
    }

    abstract fun isSuccess(p: Player, config: ConfigurationSection) : Boolean




}