package com.rainchain

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import net.mamoe.mirai.console.plugin.jvm.JvmPluginDescription
import net.mamoe.mirai.console.plugin.jvm.KotlinPlugin
import net.mamoe.mirai.contact.MemberPermission
import net.mamoe.mirai.contact.PermissionDeniedException
import net.mamoe.mirai.contact.nameCardOrNick
import net.mamoe.mirai.event.GlobalEventChannel
import net.mamoe.mirai.event.events.GroupMessageEvent
import net.mamoe.mirai.utils.ExternalResource.Companion.toExternalResource
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

object AllGroupMember : KotlinPlugin(
    JvmPluginDescription(
        id = "com.rainchain.allgroupmember",
        name = "AllGroupMember",
        version = "0.1.0",
    ) {
        author("rainchain")
    }
) {
    override fun onEnable() {
        GlobalEventChannel.filter{ event -> event is GroupMessageEvent && event.message.contentToString().startsWith("/群员列表") }
            .subscribeAlways<GroupMessageEvent> { event ->
                if ( event.sender.permission == MemberPermission.MEMBER){
                    event.group.sendMessage("『ERROR』权限不足，仅有群主或管理员可用")
                }else{
                    var memberInfo = ""
                    event.group.sendMessage("『INFO』茉莉正在生成群员信息...请稍后")
                    event.group.members.forEach{ member ->
                        run {
                            memberInfo += member.nameCardOrNick + "(" +member.id + ") 最后发言于:"+
                                    SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Date(member.lastSpeakTimestamp.toLong()*1000))
                            if (member.specialTitle!=""){
                                memberInfo += " 头衔:" + member.specialTitle + "\n"
                            } else{
                                memberInfo += "\n"
                            }
                        }
                    }
                    val file = File("./data/groupBackUp/"+event.group+"列表.txt")
                    file.writeText(memberInfo)
                    val res = file.toExternalResource(event.group.toString() + "列表.txt")
                    try {
                        event.group.files.uploadNewFile(event.group.toString() + "列表.txt",res)
                    }catch (e: PermissionDeniedException){
                        event.group.sendMessage("『ERROR』当前群仅有管理员能够上传文件")
                    }
                    withContext(Dispatchers.IO) {
                        res.close()
                    }
                }
            }
    }
}