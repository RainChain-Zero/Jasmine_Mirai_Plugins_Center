package rainchain

import net.mamoe.mirai.console.plugin.jvm.JvmPluginDescription
import net.mamoe.mirai.console.plugin.jvm.KotlinPlugin
import net.mamoe.mirai.event.GlobalEventChannel
import net.mamoe.mirai.event.events.GroupMessageEvent
import net.mamoe.mirai.message.data.MessageSource.Key.recall
import net.mamoe.mirai.message.data.PlainText
import net.mamoe.mirai.utils.info
import java.util.*
import kotlin.contracts.contract

object Antisetu : KotlinPlugin(
    JvmPluginDescription(
        id = "rainchain.antisetu",
        version = "1.1",
    )
) {
    override fun onEnable() {
        logger.info { "Antisetu and Antishuaping Plugin load" }

        //千音涩涩图回复
        GlobalEventChannel.filter { event -> event is GroupMessageEvent && event.sender.id == 959686587.toLong() }
            .subscribeAlways<GroupMessageEvent> { event ->
                if (event.message.serializeToMiraiCode().contains("1ABB4EC8-9F5F-C270-60CB-1BD614F40FBF")) {
                    event.group.sendMessage("千音酱~不可以涩涩！")
                }
            }

        var preTimeImpl: Long = 0
        var preTime = mutableMapOf<Long, Long>()
        var numImpl: Int = 1
        var num = mutableMapOf<Long, Int>()
        //反图片刷屏（>=5)
        GlobalEventChannel.filter { event -> event is GroupMessageEvent }.subscribeAlways<GroupMessageEvent> { event ->
            //在这里填写目标group
            if ((event.group.id == 921454429L || event.group.id == 801655697L) && event.sender.id != 959686587L) {
                //如果信息包含图片
                if (event.message.serializeToMiraiCode().contains("[mirai:image:")) {
                    //存在id对应的preTime
                    if (preTime.containsKey(event.sender.id)) {
                        preTimeImpl = preTime[event.sender.id]!!
                        //若间隔小于1分钟，则num++
                        if (System.currentTimeMillis() - preTimeImpl < 8 * 1000) {
                            //存在id对应的num
                            if (num.containsKey(event.sender.id)) {
                                numImpl = num[event.sender.id]!!
                                numImpl++
                                num[event.sender.id] = numImpl
                            }
                            //不存在，则对应id记为1
                            else {
                                num[event.sender.id] = 1
                            }
                            preTimeImpl = System.currentTimeMillis()
                            preTime[event.sender.id] = preTimeImpl
                        }
                        //间隔大于1分钟
                        else {
                            numImpl = 1
                            num[event.sender.id] = 1
                            preTimeImpl = System.currentTimeMillis()
                            preTime[event.sender.id] = preTimeImpl
                        }
                        //触发刷屏
                        if (numImpl >= 5) {
                            if (event.group.botPermission > event.sender.permission) {
                                event.sender.mute(600)
                                event.group.sendMessage(PlainText("诶，主人说过刷屏的是坏孩子哦？先休息一会啦"))
                            } else {
                                if (event.subject == event.bot) {
                                } else {
                                    event.group.sendMessage(
                                        PlainText("茉莉觉得").plus(PlainText(event.sender.nick)).plus(PlainText("不能带头刷屏哦？"))
                                    )
                                }
                                preTime[event.sender.id] = System.currentTimeMillis()
                            }
                        }
                    }
                    //不存在对应的preTime
                    else {
                        preTime[event.sender.id] = System.currentTimeMillis()
                        num[event.sender.id] = 1
                    }
                }
            }
        }
    }
}