package rainchain.moli

import net.mamoe.mirai.console.plugin.jvm.JvmPluginDescription
import net.mamoe.mirai.console.plugin.jvm.KotlinPlugin
import net.mamoe.mirai.event.GlobalEventChannel
import net.mamoe.mirai.event.events.FriendEvent
import net.mamoe.mirai.event.events.GroupEvent
import net.mamoe.mirai.event.events.GroupMessageEvent
import net.mamoe.mirai.event.events.NudgeEvent
import net.mamoe.mirai.event.subscribeStrangerMessages
import net.mamoe.mirai.message.data.*
import net.mamoe.mirai.utils.info

object Notice : KotlinPlugin(
    JvmPluginDescription(
        id = "rainchain.moli.Notice",
        version = "1.0",
    )
) {
    override fun onEnable() {
        logger.info { "Notice Plugin loaded" }
        val channel1 =GlobalEventChannel
        val channel2 =GlobalEventChannel.filter { event->
            event is GroupMessageEvent
        }
        channel1.subscribeAlways<NudgeEvent> { event->
            if(event.target.id==3349795206L)
            {
                //是master的个人讲话！
                val topic1=PlainText("首先是茉莉master——Ashiterui以及运维大爹——秋サンマの个人讲话部分~")
                val speak1=PlainText("非常感谢各位过去一年的陪伴和支持#鞠躬\n" +
                        "对茉莉的喜欢令我们非常高兴，新的一年同样还请多多关照，从今往后，也要一路走下去。那么，祝大家新的一年，坚信自己所爱，触及自己心中所梦，一路扬帆。\n" +
                        "最后，同样感谢孩子她妈和运维大爹的技术及资源支持，新的一年也要一起继续加油\n" +
                        "(ps:是大更新哦)")
                //是运维大爹的个人讲话！
                val speak2=PlainText("非常荣幸能在过去的一年里与二位dalao进行合作，期待在新的一年里继续以项目参与者的视角见证茉莉的成长，也希望大家在新的一年里能更加更加喜欢上茉莉")
                val speak3=PlainText("那么，就由我来给大家介绍一下本次更新的内容~")
                //更新内容
                val chain1=PlainText("Bug及意见反馈以及各种摸鱼群：921454429\n欢迎各位来玩ヾ(´∀｀。ヾ)")
                val chain2=PlainText("文案&策划:Ashiterui(2677409596)\n" +
                        "Lua&Kotlin代码实现：RainChain(3032902237)\n" +
                        "运维大爹：秋サンマ（2225336268）")
                val chain3=PlainText("[好感互动模块V4.5&其他功能更新通告]\n" +
                        "①.剧情模式第一章开放!!!进入指令为”进入剧情 第一章“,需要使用道具才能解锁哦\n" +
                        "②.新功能『商店』开放!需要在第一章剧情中自行探索解锁条件哦\n" +
                        "③.伴随着商店的开放，将会解锁道具&礼物系统\n" +
                        "④.交易系统开放！可支持用户之间进行道具交易（使用方法见下文指令部分）\n" +
                        "⑤.为了适应本次版本更新，我们小幅度削减了部分交互增加的好感度并小幅度增加了好感度随时间降低的力度\n" +
                        "⑥.其他：明日方舟TRPG规则检定、暗骰功能重开放、wolfram-alpha高性能（？科学计算器、来点二次元")
                val chain4=PlainText("您可能需要特别注意的部分!!\n\n" +
                        "一.第一章的剧情模式设立了复杂的选项，你的选择将会大幅度影响当前以及后续章节的剧情走向，甚至影响到体验，我们鼓励大家积极尝试不同的路线!!!\n\n" +
                        "二.部分物品效果不 可 叠 加，请特别留意\n\n" +
                        "三.私聊茉莉需要添加茉莉为好友哦，茉莉很开心和大家做朋友的\n\n" +
                        "四.“.rh”暗骰功能已在茉莉重新开放，加入了鉴权，只有好友能触发暗骰\n（注意!!除非确认暗骰不会导致骰娘被冻结，请在使用时关闭其他骰娘）")
                val chain5=PlainText("----下面是本次新增的指令集，您可以随时查阅----\n(注：输入“.help 好感交互”可以查看有关帮助)")
                val chain6=PlainText("1.“进入商店”：进入商店购买页面，需要在第一章中解锁\n" +
                        "2.”.u 道具名“用于使用非礼物性道具\n" +
                        "3.”赠送茉莉 （数量） 礼物名“：赠送茉莉礼物，数量不填默认为1\n" +
                        "4.“购买 （数量） 物品”：购买指定数量的物品，数量不填默认为1\n" +
                        "5.“道具图鉴”：就如同字面意思一样\n" +
                        "6.“查询 道具名”：查询所持有的该道具数量以及详细信息\n" +
                        "7.交易系统部分（重要！）:请查阅下方图片部分!").plus(Image("{98CC1E00-40BB-0D4B-5FCA-76928554A9EF}.png")) .plus(
                    PlainText("8.明日方舟TRPG规则检定：输入“.help rk”查看详细信息\n" +
                            "9.wolfram-alpha：用于科学计算，请勿滥用!!开头指令为“/c”，具体使用请百度wolfram-alpha，示例:\n" +
                            "输入/c Integrate[1/(1＋x^7),{x}] 计算不定积分\n" +
                            "10.“来点二次元”：接口不稳定，还在测试中，可能响应速度和图片有待提高"))
                val chain7=PlainText("那么，本次公告就到此结束啦，祝各位新年快乐~新的一年也请多多指教了哦")
                val content1=ForwardMessage.Node(3349795206L,1643644804,"茉莉",topic1)
                val content2=ForwardMessage.Node(2677409596L,1643644804,"Ashiterui",speak1)
                val content3=ForwardMessage.Node(2225336268L,1643644804,"秋サンマ",speak2)
                val content4=ForwardMessage.Node(3032902237L,1643644804,"RainChain",speak3)
                val content5=ForwardMessage.Node(3349795206L,1643644804,"茉莉",chain1)
                val content6=ForwardMessage.Node(3349795206L,1643644804,"茉莉",chain2)
                val content7=ForwardMessage.Node(3349795206L,1643644804,"茉莉",chain3)
                val content8=ForwardMessage.Node(3349795206L,1643644804,"茉莉",chain4)
                val content9=ForwardMessage.Node(3349795206L,1643644804,"茉莉",chain5)
                val content10=ForwardMessage.Node(3349795206L,1643644804,"茉莉",chain6)
                val content11=ForwardMessage.Node(3349795206L,1643644804,"茉莉",chain7)
                val msg= listOf<ForwardMessage.Node>(content1,content2,content3,content5,content6,content4,content7,content8,content9,content10,content11)
                val preview= listOf<String>("茉莉:首先是茉莉master——Ashiterui以及运维大爹——秋サンマの个人讲话部分~","Ashiterui:非常感谢各位过去一年的陪伴和支持#鞠躬\n" +
                        "对茉莉的喜欢令我们非常高兴，新的一年同样还请多多关照，从今往后，也要一路走下去。那么，祝大家新的一年，坚信自己所爱，触及自己心中所梦，一路扬帆。\n" +
                        "最后，同样感谢孩子她妈和运维大爹的技术及资源支持，新的一年也要一起继续加油\n" +
                        "(ps:是大更新哦)")
                val title="是茉莉の更新公告!!!"
                val brief="更新公告"
                val source="聊天记录"
                val summary="查看更新公告"
                event.subject.sendMessage(ForwardMessage(preview,title,brief,source,summary,msg))
            }
        }
        channel2.subscribeAlways<GroupMessageEvent> { event->
            if (event.message.contentToString().contains("@3349795206"))
            {
                if (event.message.contentToString().contains("更新"))
                {
                    //是master的个人讲话！
                    val topic1=PlainText("首先是茉莉master——Ashiterui以及运维大爹——秋サンマの个人讲话部分~")
                    val speak1=PlainText("非常感谢各位过去一年的陪伴和支持#鞠躬\n" +
                            "对茉莉的喜欢令我们非常高兴，新的一年同样还请多多关照，从今往后，也要一路走下去。那么，祝大家新的一年，坚信自己所爱，触及自己心中所梦，一路扬帆。\n" +
                            "最后，同样感谢孩子她妈和运维大爹的技术及资源支持，新的一年也要一起继续加油\n" +
                            "(ps:是大更新哦)")
                    //是运维大爹的个人讲话！
                    val speak2=PlainText("非常荣幸能在过去的一年里与二位dalao进行合作，期待在新的一年里继续以项目参与者的视角见证茉莉的成长，也希望大家在新的一年里能更加更加喜欢上茉莉")
                    val speak3=PlainText("那么，就由我来给大家介绍一下本次更新的内容~")
                    //更新内容
                    val chain1=PlainText("Bug及意见反馈以及各种摸鱼群：921454429\n欢迎各位来玩ヾ(´∀｀。ヾ)")
                    val chain2=PlainText("文案&策划:Ashiterui(2677409596)\n" +
                            "Lua&Kotlin代码实现：RainChain(3032902237)\n" +
                            "运维大爹：秋サンマ（2225336268）")
                    val chain3=PlainText("[好感互动模块V4.5&其他功能更新通告]\n" +
                            "①.剧情模式第一章开放!!!进入指令为”进入剧情 第一章“,需要使用道具才能解锁哦\n" +
                            "②.新功能『商店』开放!需要在第一章剧情中自行探索解锁条件哦\n" +
                            "③.伴随着商店的开放，将会解锁道具&礼物系统\n" +
                            "④.交易系统开放！可支持用户之间进行道具交易（使用方法见下文指令部分）\n" +
                            "⑤.为了适应本次版本更新，我们小幅度削减了部分交互增加的好感度并小幅度增加了好感度随时间降低的力度\n" +
                            "⑥.其他：明日方舟TRPG规则检定、暗骰功能重开放、wolfram-alpha高性能（？科学计算器、来点二次元")
                    val chain4=PlainText("您可能需要特别注意的部分!!\n\n" +
                            "一.第一章的剧情模式设立了复杂的选项，你的选择将会大幅度影响当前以及后续章节的剧情走向，甚至影响到体验，我们鼓励大家积极尝试不同的路线!!!\n\n" +
                            "二.部分物品效果不 可 叠 加，请特别留意\n\n" +
                            "三.私聊茉莉需要添加茉莉为好友哦，茉莉很开心和大家做朋友的\n\n" +
                            "四.“.rh”暗骰功能已在茉莉重新开放，加入了鉴权，只有好友能触发暗骰\n（注意!!除非确认暗骰不会导致骰娘被冻结，请在使用时关闭其他骰娘）")
                    val chain5=PlainText("----下面是本次新增的指令集，您可以随时查阅----\n(注：输入“.help 好感交互”可以查看有关帮助)")
                    val chain6=PlainText("1.“进入商店”：进入商店购买页面，需要在第一章中解锁\n" +
                            "2.”.u 道具名“用于使用非礼物性道具\n" +
                            "3.”赠送茉莉 （数量） 礼物名“：赠送茉莉礼物，数量不填默认为1\n" +
                            "4.“购买 （数量） 物品”：购买指定数量的物品，数量不填默认为1\n" +
                            "5.“道具图鉴”：就如同字面意思一样\n" +
                            "6.“查询 道具名”：查询所持有的该道具数量以及详细信息\n" +
                            "7.交易系统部分（重要！）:请查阅下方图片部分!").plus(Image("{98CC1E00-40BB-0D4B-5FCA-76928554A9EF}.png")) .plus(
                        PlainText("8.明日方舟TRPG规则检定：输入“.help rk”查看详细信息\n" +
                                "9.wolfram-alpha：用于科学计算，请勿滥用!!开头指令为“/c”，具体使用请百度wolfram-alpha，示例:\n" +
                                "输入/c Integrate[1/(1＋x^7),{x}] 计算不定积分\n" +
                                "10.“来点二次元”：接口不稳定，还在测试中，可能响应速度和图片有待提高"))
                    val chain7=PlainText("那么，本次公告就到此结束啦，祝各位新年快乐~新的一年也请多多指教了哦")
                    val content1=ForwardMessage.Node(3349795206L,1643644804,"茉莉",topic1)
                    val content2=ForwardMessage.Node(2677409596L,1643644804,"Ashiterui",speak1)
                    val content3=ForwardMessage.Node(2225336268L,1643644804,"秋サンマ",speak2)
                    val content4=ForwardMessage.Node(3032902237L,1643644804,"RainChain",speak3)
                    val content5=ForwardMessage.Node(3349795206L,1643644804,"茉莉",chain1)
                    val content6=ForwardMessage.Node(3349795206L,1643644804,"茉莉",chain2)
                    val content7=ForwardMessage.Node(3349795206L,1643644804,"茉莉",chain3)
                    val content8=ForwardMessage.Node(3349795206L,1643644804,"茉莉",chain4)
                    val content9=ForwardMessage.Node(3349795206L,1643644804,"茉莉",chain5)
                    val content10=ForwardMessage.Node(3349795206L,1643644804,"茉莉",chain6)
                    val content11=ForwardMessage.Node(3349795206L,1643644804,"茉莉",chain7)
                    val msg= listOf<ForwardMessage.Node>(content1,content2,content3,content5,content6,content4,content7,content8,content9,content10,content11)
                    val preview= listOf<String>("茉莉:首先是茉莉master——Ashiterui以及运维大爹——秋サンマの个人讲话部分~","Ashiterui:非常感谢各位过去一年的陪伴和支持#鞠躬\n" +
                            "对茉莉的喜欢令我们非常高兴，新的一年同样还请多多关照，从今往后，也要一路走下去。那么，祝大家新的一年，坚信自己所爱，触及自己心中所梦，一路扬帆。\n" +
                            "最后，同样感谢孩子她妈和运维大爹的技术及资源支持，新的一年也要一起继续加油\n" +
                            "(ps:是大更新哦)")
                    val title="是茉莉の更新公告!!!"
                    val brief="更新公告"
                    val source="聊天记录"
                    val summary="查看更新公告"
                    event.group.sendMessage(ForwardMessage(preview,title,brief,source,summary,msg))
                }
            }
        }
    }

}