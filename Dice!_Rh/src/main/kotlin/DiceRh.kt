package rainchain

import net.mamoe.mirai.Bot
import net.mamoe.mirai.console.plugin.jvm.JvmPluginDescription
import net.mamoe.mirai.console.plugin.jvm.KotlinPlugin
import net.mamoe.mirai.event.GlobalEventChannel
import net.mamoe.mirai.event.events.FriendMessageEvent
import net.mamoe.mirai.event.events.GroupMessageEvent
import net.mamoe.mirai.event.events.MessageEvent
import net.mamoe.mirai.event.globalEventChannel
import net.mamoe.mirai.message.data.MessageChain
import net.mamoe.mirai.message.data.PlainText
import net.mamoe.mirai.message.data.buildMessageChain
import net.mamoe.mirai.utils.info

object DiceRh : KotlinPlugin(
    JvmPluginDescription(
        id = "RainChain.DiceRh",
        version = "1.0",
    )
) {
    override fun onEnable() {
        logger.info { "Dice!_Rh Plugin loaded" }

        val channel1= GlobalEventChannel.filter { event->
            event is GroupMessageEvent }
        channel1.subscribeAlways<GroupMessageEvent> { event->
            if(message.contentToString().equals(".rh") or message.contentToString().equals("。rh"))
            {
                val res=(1..100).random()
                if(event.bot.getFriend(event.sender.id)!=null)
                {
                    val chain1=PlainText("你已进行了一次暗骰")
                    event.group.sendMessage(chain1)
                    val chain2=PlainText("暗骰结果:1D100=").plus(PlainText(res.toString()))
                    event.sender.sendMessage(chain2)
                }
                else
                {
                    event.group.sendMessage("临时会话暗骰已被管理员锁定，请先添加本机好友哦~")
                }
            }
        }
        val channel2=GlobalEventChannel.filter { event->
            event is FriendMessageEvent
        }
        channel2.subscribeAlways<FriendMessageEvent> { event->
            if(message.contentToString().equals(".rh") or message.contentToString().equals("。rh"))
            {
                val res=(1..100).random()
                val chain2=PlainText(event.senderName).plus(PlainText("进行暗骰:1D100=")).plus(res.toString())
                event.sender.sendMessage(chain2)
            }
        }
    }
}