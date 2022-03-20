package rainchain

import Utils.OkhttpUtil
import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.JSONObject
import kotlinx.serialization.json.JsonObject
import net.mamoe.mirai.console.plugin.jvm.JvmPluginDescription
import net.mamoe.mirai.console.plugin.jvm.KotlinPlugin
import net.mamoe.mirai.event.GlobalEventChannel
import net.mamoe.mirai.event.events.MessageEvent
import net.mamoe.mirai.message.data.ForwardMessage
import net.mamoe.mirai.message.data.PlainText
import net.mamoe.mirai.utils.info
import rainchain.Entity.Cnmods
import rainchain.Entity.Dicecho

object Searchmodule : KotlinPlugin(
    JvmPluginDescription(
        id = "rainchain.searchmodule",
        name = "SearchModule",
        version = "1.0",
    ) {
        author("RainChain")
    }
) {
    //http工具类
    private val httpUtil: OkhttpUtil = OkhttpUtil()

    override fun onEnable() {
        logger.info { "SearchModule loaded" }

        GlobalEventChannel.filter { event ->
            event is MessageEvent && event.message.contentToString().startsWith("/搜模组")
        }.subscribeAlways<MessageEvent> { event ->

            //提取模组名
            val module: String? = Regex("\\s*(.*)").find(event.message.contentToString(), 5)?.value

            //魔都搜索结果
            val cnmodsJsonObject: JSONObject =
                JSON.parseObject(httpUtil.run("https://www.cnmods.net/index/moduleListPage.do?page=1&title=$module"))
            val cnmodsRes = Cnmods(cnmodsJsonObject).getCnmodsRes()

            //DICECHO搜索结果
            val dicechoJsonObject: JSONObject =
                JSON.parseObject(httpUtil.run("https://www.dicecho.com/api/mod?keyword=$module"))
            val dicechoRes = Dicecho(dicechoJsonObject).getDicechoRes()

            //构建转发消息
            val node1 = ForwardMessage.Node(3349795206L, 0, "茉莉", PlainText("魔都搜索结果——"))
            val node2 = ForwardMessage.Node(3349795206L, 0, "茉莉", PlainText(cnmodsRes))
            val node3 = ForwardMessage.Node(3349795206L, 0, "茉莉", PlainText("DICECHO搜索结果——"))
            val node4 = ForwardMessage.Node(3349795206L, 0, "茉莉", PlainText(dicechoRes))

            event.subject.sendMessage(
                ForwardMessage(
                    listOf("为什么不点开看看呢?"),
                    "查询结果",
                    "模组查询结果",
                    "聊天记录",
                    "模组查询结果",
                    listOf(node1, node2, node3, node4)
                )
            )
        }
    }
}