package com.example

import net.mamoe.mirai.console.plugin.jvm.JvmPluginDescription
import net.mamoe.mirai.console.plugin.jvm.KotlinPlugin
import net.mamoe.mirai.event.Event
import net.mamoe.mirai.event.GlobalEventChannel
import net.mamoe.mirai.event.events.FriendMessageEvent
import net.mamoe.mirai.event.events.GroupMessageEvent
import net.mamoe.mirai.message.data.MessageSource.Key.quote
import net.mamoe.mirai.message.data.at
import net.mamoe.mirai.utils.info

object NkGame : KotlinPlugin(
    JvmPluginDescription(
        id = "com.rainchain.nk",
        name = "NkGame",
        version = "0.1.0",
    ) {
        author("RainChain")
    }
) {
    override fun onEnable() {
        logger.info { "NkGame Plugin loaded" }
        //标记游戏是否开始,group->Boolean
        val isGameStart = mutableMapOf<Long,Boolean>()
        //标记参加游戏的人员,player->group
        val playerGroup = mutableMapOf<Long,Long>()
        //存放游戏关键词待选池,group->keywords->player's name
        val keyWords = mutableMapOf<Long,MutableMap<String,String>>()
        //存放游戏数据,group->player->keyword
        val gameData = mutableMapOf<Long,MutableMap<Long,String>>()

        //参与者加入游戏
        GlobalEventChannel.filter { event: Event ->
            event is GroupMessageEvent && event.message.contentToString().startsWith("/加入nk")
        }.subscribeAlways<GroupMessageEvent> { event ->
            //第一位加入游戏
            if (!gameData.containsKey(event.group.id)) {
                //初始化游戏数据
                gameData[event.group.id]  = mutableMapOf(event.sender.id to "")
                keyWords[event.group.id] = mutableMapOf()
                playerGroup[event.sender.id] = event.group.id
                event.group.sendMessage(event.message.quote()+"你是第一个加入游戏的玩家,请等待其他人加入游戏...\n你现在可以向待选池中添加关键词,格式为:/添加nk 关键词")
                return@subscribeAlways
            }
            //重复加入
            if (playerGroup.containsKey(event.sender.id)) {
                event.group.sendMessage(event.message.quote()+"你已经加入一个游戏了哦~")
                return@subscribeAlways
            }
            gameData[event.group.id]  = mutableMapOf(event.sender.id to "")
            playerGroup[event.sender.id] = event.group.id
            event.group.sendMessage(event.message.quote()+"你已经成功加入了本局游戏√")
        }
        //添加关键词
        GlobalEventChannel.filter { event: Event ->
            event is FriendMessageEvent && event.message.contentToString().startsWith("/添加nk")
                    && playerGroup.containsKey(event.sender.id)
        }.subscribeAlways<FriendMessageEvent> { event ->
            val messageOri = event.message.contentToString().substring(5)
            val keyWordsList = messageOri.split("%s+".toRegex())
            val group = playerGroup[event.sender.id]!!
            logger.info("已添加关键词：$keyWordsList")
            keyWordsList.forEach(){
                if (keyWords[group]?.containsKey(it) == false) {
                    keyWords[group]?.put(it,event.sender.nick)
                }
            }
            event.friend.sendMessage("关键词添加成功√")
        }
        //开始游戏
        GlobalEventChannel.filter { event: Event ->
            event is GroupMessageEvent && event.message.contentToString().startsWith("/开始nk")
                    && gameData[event.group.id]?.containsKey(event.sender.id) == true
        }.subscribeAlways<GroupMessageEvent> { event ->
            if (isGameStart[event.group.id] == true) {
                event.group.sendMessage("游戏已经开始了哦~")
                return@subscribeAlways
            }
            if ((keyWords[event.group.id]?.size ?: 0) < gameData[event.group.id]!!.keys.size) {
                event.group.sendMessage("关键词数量不足,请添加关键词后再开始游戏")
                return@subscribeAlways
            }
            isGameStart[event.group.id] = true
            //当前关键词池列表
            val keyWordsList = keyWords[event.group.id]?.keys?.toList()
            logger.info("当前关键词池：$keyWordsList")
            //随机打乱分配
            val keyWordsRandom = keyWordsList?.shuffled()?.take(gameData[event.group.id]!!.keys.size)
            logger.info("随机分配关键词池：$keyWordsRandom")
            var index = 0
            gameData[event.group.id]?.forEach { (player, _) ->
                gameData[event.group.id]?.set(player,keyWordsRandom?.get(index) ?: "")
                index++
            }
            logger.info("最终关键词：${gameData[event.group.id].toString()}")
            event.group.sendMessage("游戏正式开始——开始前记得看看其他人都分到了什么关键词哦~")
        }
        //检测是否有人说了关键词
        GlobalEventChannel.filter { event: Event ->
            event is GroupMessageEvent && isGameStart[event.group.id] == true
                    && gameData[event.group.id]?.containsKey(event.sender.id) == true
        }.subscribeAlways<GroupMessageEvent> { event ->
            val message = event.message.contentToString()
            val keyword = gameData[event.group.id]?.get(event.sender.id)?:""
            if (message.contains(keyword)) {
                event.group.sendMessage(event.message.quote()+"#少女坏笑着把你头上的纸片撕下，在你眼前晃了晃\n" +
                        "上面赫然印着——${keyword}！还有一行签名：${keyWords[event.group.id]?.get(keyword)?:""}")
                gameData[event.group.id]?.remove(event.sender.id)
                playerGroup.remove(event.sender.id)
            }
            if ((gameData[event.group.id]?.keys?.size ?: 0) <= 1){
                event.group.sendMessage("游戏结束,恭喜${gameData[event.group.id]?.keys?.
                    firstOrNull()?.let { event.group[it] }?.nick}获得胜利!")
                gameData.remove(event.group.id)
                keyWords.remove(event.group.id)
                playerGroup.remove(event.sender.id)
                isGameStart.remove(event.group.id)
            }
        }
        //结束游戏
        GlobalEventChannel.filter { event: Event ->
            event is GroupMessageEvent && isGameStart[event.group.id] == true
                    && event.message.contentToString().startsWith("/结束nk")
                    && gameData[event.group.id]?.containsKey(event.sender.id) == true
        }.subscribeAlways<GroupMessageEvent> { event ->
            gameData.remove(event.group.id)
            keyWords.remove(event.group.id)
            playerGroup.remove(event.sender.id)
            isGameStart.remove(event.group.id)
            event.group.sendMessage(event.message.quote()+"游戏已经结束啦——")
        }
        //退出游戏
        GlobalEventChannel.filter { event: Event ->
            event is GroupMessageEvent && isGameStart[event.group.id] == true
                    && event.message.contentToString().startsWith("/退出nk")
                    && gameData[event.group.id]?.containsKey(event.sender.id) == true
        }.subscribeAlways<GroupMessageEvent> { event ->
            gameData[event.group.id]?.remove(event.sender.id)
            playerGroup.remove(event.sender.id)
            event.group.sendMessage(event.message.quote()+"你已经退出本群的游戏")
        }
    }
}