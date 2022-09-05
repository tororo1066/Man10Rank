package tororo1066.man10rank.playTime

import org.bukkit.Bukkit
import tororo1066.man10rank.Man10Rank
import tororo1066.tororopluginapi.utils.toPlayer
import java.util.UUID

class PlayTimeCounter {

    init {
        init()
    }

    private fun init(){

        Bukkit.getScheduler().runTaskTimer(Man10Rank.plugin, Runnable {
            val users = ArrayList<String>()
            Man10Rank.userData.forEach { (_, data) ->
                data.loginTime += 5
                users.add("'" + data.uuid.toString() + "'")
                data.checkRankUp()
            }
            if (users.isNotEmpty()){

                Man10Rank.mysql.asyncExecute("update user_data set time = time+5 where ${users.joinToString {"or uuid = $it "} }".replaceFirst("or ",""))
            }
        },6000,6000)

    }


}