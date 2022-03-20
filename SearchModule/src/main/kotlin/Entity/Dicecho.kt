package rainchain.Entity

import com.alibaba.fastjson.JSONArray
import com.alibaba.fastjson.JSONObject


/**
 * @author  慕北_Innocent
 * @date  2022/3/20 21:22
 * @version 1.0
 */
class Dicecho(private val dicechoJsonObject: JSONObject) {

    fun getDicechoRes(): String {
        if (!(dicechoJsonObject["success"] as Boolean)) {
            return "『✖Error!』DICECHO模组搜索模组失败！状态码:" + dicechoJsonObject["code"].toString()
        }

        val dataObject: JSONObject = dicechoJsonObject["data"] as JSONObject
        val dataJsonArray:JSONArray=dataObject["data"] as JSONArray

        //没有结果
        if (dataJsonArray.size == 0) {
            return "『✖Error!』茉莉在DICECHO中未找到相关结果呢..."
        }

        val len = if (dataJsonArray.size >= 3) {
            3
        } else {
            dataJsonArray.size
        }

        var res = ""

        for (i in 0 until len) {
            val moduleObject: JSONObject = dataJsonArray[i] as JSONObject
            val title = moduleObject["title"]  //标题
            val releaseDate = moduleObject["releaseDate"]   //发布时间
            val authorObject: JSONObject = moduleObject["author"] as JSONObject
            val nickName = authorObject["nickName"]   //作者
            val moduleRule = moduleObject["moduleRule"] //分类
            val playerNumberObject: JSONArray = moduleObject["playerNumber"] as JSONArray
            val playerNumber0 = playerNumberObject[0] //最小人数
            val playerNumber1 = playerNumberObject[1] //最多人数
            val description = moduleObject["description"]  //简介
            val originUrl = moduleObject["originUrl"]  //原链接

            res = res + "『$title』|$releaseDate\n\n投稿人:$nickName|分类:$moduleRule\n\n人数:$playerNumber0~$playerNumber1 人" +
                    "\n\n原地址:$originUrl"
            if (i < len - 1) {
                res += "\n===============\n"
            }
        }

        return res
    }
}