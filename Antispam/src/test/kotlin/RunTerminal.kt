package rainchain

import java.io.File
import net.mamoe.mirai.alsoLogin
import net.mamoe.mirai.console.MiraiConsole
import net.mamoe.mirai.console.plugin.PluginManager.INSTANCE.enable
import net.mamoe.mirai.console.plugin.PluginManager.INSTANCE.load
import net.mamoe.mirai.console.terminal.MiraiConsoleTerminalLoader

fun setupWorkingDir() {
    // see: net.mamoe.mirai.console.terminal.MiraiConsoleImplementationTerminal
    System.setProperty("user.dir", File("debug-sandbox").absolutePath)
}

suspend fun main() {
    setupWorkingDir()

    MiraiConsoleTerminalLoader.startAsDaemon()

    val pluginInstance = Antisetu

    pluginInstance.load() // 主动加载插件, Console 会调用 Antisetu.onLoad
    pluginInstance.enable() // 主动启用插件, Console 会调用 Antisetu.onEnable

    val bot = MiraiConsole.addBot(123, "123").alsoLogin() // 登录一个测试环境的 Bot

    MiraiConsole.job.join()
}
