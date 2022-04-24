package meme

import meme.JudgeFavor
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import java.io.*
import java.lang.Exception
import java.nio.charset.Charset

/**
 * @author 慕北_Innocent
 * @version 1.0
 * @date 2022/04/23 11:17
 */
class JudgeFavor {
    //读取整个文件
    fun readToString(fileName: String?): String? {
        val encoding = "UTF-8"
        val file = File(fileName)
        val filelength = file.length()
        val filecontent = ByteArray(filelength.toInt())
        try {
            val `in` = FileInputStream(file)
            `in`.read(filecontent)
            `in`.close()
        } catch (e: FileNotFoundException) {
            return null
        } catch (e: IOException) {
            return null
        }
        return try {
            String(filecontent, Charset.forName("UTF-8"))
        } catch (e: UnsupportedEncodingException) {
            null
        }
    }

    //判断好感是否达到3000
    fun judgeFavor(QQ: Long, favorLimit: Int): Boolean {
        val jsonStr = readToString("$UserConfPath$QQ/favorConf.json") ?: return false
        return try {
            val gs = GsonBuilder().setPrettyPrinting().create()
            val favorElement = gs.fromJson(jsonStr, JsonObject::class.java)["好感度"]
            if (favorElement != null) {
                val favor = favorElement.asInt
                return favor >= favorLimit
            }
            false
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    companion object {
        //用户配置文件
        const val UserConfPath = "/home/mirai/Dice3349795206/UserConfDir/"
    }
}