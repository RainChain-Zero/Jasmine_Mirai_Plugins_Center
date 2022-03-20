package rainchain.onlinecourse

import Utils.OkhttpUtil
import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.JSONObject
import net.mamoe.mirai.console.plugin.jvm.JvmPluginDescription
import net.mamoe.mirai.console.plugin.jvm.KotlinPlugin
import net.mamoe.mirai.event.GlobalEventChannel
import net.mamoe.mirai.event.events.MessageEvent
import net.mamoe.mirai.message.data.PlainText
import net.mamoe.mirai.utils.info
import rainchain.onlinecourse.Utils.UnicodeUtil

object Plugin : KotlinPlugin(
    JvmPluginDescription(
        id = "rainchain.onlinecourse.plugin",
        name = "OnlineCourseAnswers",
        version = "1.0",
    ) {
        author("RainChain-Zero")
    }
) {
    //token
    private const val token = "free"

    //工具类实例
    private val okhttpUtil = OkhttpUtil()
    private val unicodeUtil = UnicodeUtil()

    override fun onEnable() {
        logger.info { "OnlineCourse Plugin loaded" }

        GlobalEventChannel.filter { event -> event is MessageEvent }.subscribeAlways<MessageEvent> { event ->
            val msg = event.message.contentToString()
            if (msg.startsWith("/q") || msg.startsWith("/Q")) {
                val question: String? = Regex("\\s*(.*)").find(msg, 3)?.value
                if (question != null) {
                    val responseJson =
                        okhttpUtil.run("https://wangke.hive-net.cn/wechat/search/?token=free&question=$question")
                    val responseJsonObject: JSONObject = JSON.parseObject(responseJson)

                    if (responseJsonObject["has_reason"] as Int == 1) {
                        event.subject.sendMessage(PlainText(unicodeUtil.decode(responseJsonObject["reason"] as String)))
                    } else {
                        event.subject.sendMessage(PlainText("唔姆，茉莉暂时没有找到该问题的答案呢..."))
                    }
                }
            }
        }
    }
}