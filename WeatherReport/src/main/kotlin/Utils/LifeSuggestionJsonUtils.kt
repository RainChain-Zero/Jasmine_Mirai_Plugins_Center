package rainchain.Utils

import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.JSONArray
import com.alibaba.fastjson.JSONObject
import com.alibaba.fastjson.JSONPObject
import java.util.*

/**
 * @author  慕北_Innocent
 * @date  2022/2/11 19:28
 * @version 1.0
 */
class LifeSuggestionJsonUtils(private val lifeSuggestionJsonObject: JSONObject) {

    fun getLifeSuggestion(): String {
        val lifeSuggestionJsonArray: JSONArray = lifeSuggestionJsonObject["daily"] as JSONArray
        var res: String = "当日生活指数：\n"
        var lifeSuggestionJSONObj: JSONObject

        for (cnt in lifeSuggestionJsonArray.indices) {
            lifeSuggestionJSONObj = lifeSuggestionJsonArray[cnt] as JSONObject
            var name = lifeSuggestionJSONObj["name"] as String
            var cate = lifeSuggestionJSONObj["category"] as String
            var text = lifeSuggestionJSONObj["text"] as String

            res = "$res# $name:$cate\n$text\n\n"
        }
        return res
    }


}