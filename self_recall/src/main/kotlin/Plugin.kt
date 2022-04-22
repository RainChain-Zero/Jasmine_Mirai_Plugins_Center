package rainchain

import net.mamoe.mirai.console.plugin.jvm.JvmPluginDescription
import net.mamoe.mirai.console.plugin.jvm.KotlinPlugin
import net.mamoe.mirai.event.GlobalEventChannel
import net.mamoe.mirai.event.events.MessageEvent
import net.mamoe.mirai.message.data.QuoteReply
import net.mamoe.mirai.message.data.recallSource
import net.mamoe.mirai.message.data.source
import net.mamoe.mirai.utils.info

object Plugin : KotlinPlugin(
    JvmPluginDescription(
        id = "rainchain.plugin",
        name = "self_recall",
        version = "1.0",
    ) {
        author("RainChain")
    }
) {
    override fun onEnable() {
        logger.info { "self_recall plugin loaded" }

        GlobalEventChannel.filter { event -> event is MessageEvent && event.message.contentToString().contains("撤回") }
            .subscribeAlways<MessageEvent> { event ->
                event.message.forEach { message ->
                    if (message is QuoteReply && message.source.targetId == event.bot.id) {
                        message.recallSource()
                    }
                }
            }
    }
}