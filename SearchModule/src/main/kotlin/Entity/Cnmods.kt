package rainchain.Entity

import com.alibaba.fastjson.JSONArray
import com.alibaba.fastjson.JSONObject
import rainchain.Searchmodule


/**
 * @author  慕北_Innocent
 * @date  2022/3/20 20:40
 * @version 1.0
 */
class Cnmods(private val cnmodsJsonObject: JSONObject) {

    fun getCnmodsRes(): String {
        //判断请求是否成功
        if (cnmodsJsonObject["code"] != 1) {
            Searchmodule.logger.warning("魔都搜索模组失败！状态码:" + cnmodsJsonObject["code"].toString())
            return "『✖Error!』魔都搜索模组失败！状态码:" + cnmodsJsonObject["code"].toString()
        }

        val dataJsonObject: JSONObject = cnmodsJsonObject["data"] as JSONObject
        val dataJsonArray: JSONArray = dataJsonObject["list"] as JSONArray
        var res = ""
        //没有结果
        if (dataJsonArray.size == 0) {
            return "『✖Error!』茉莉在魔都中未找到相关结果呢..."
        }

        val len = if (dataJsonArray.size >= 3) {
            3
        } else {
            dataJsonArray.size
        }

        for (i in 0 until len) {
            val moduleObject: JSONObject = dataJsonArray[i] as JSONObject
            val title = moduleObject["title"]  //标题
            val releaseDate = moduleObject["releaseDate"]  //发布时间
            val article = moduleObject["article"]  //作者
            val moduleVersion = moduleObject["moduleVersion"]   //版本
            val moduleType = moduleObject["moduleType"]  //分类
            val minDuration = moduleObject["minDuration"]  //最短时间
            val maxDuration = moduleObject["maxDuration"]   //最长时间
            val minAmount = moduleObject["minAmount"]   //最少人数
            val maxAmount = moduleObject["maxAmount"]  //最多人数
            val opinion = moduleObject["opinion"]   //简介
            val url = moduleObject["url"]   //下载链接

            res = res + "『$title』|$releaseDate\n\n投稿人:$article|分类:$moduleVersion、$moduleType\n\n时长:$minDuration~" +
                    "$maxDuration h|人数:$minAmount~$maxAmount 人\n\n简介:$opinion\n\n下载链接:$url"
            if (i < len - 1) {
                res += "\n===============\n"
            }
        }
        return res
    }
}