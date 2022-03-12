package rainchain.Utils

import com.alibaba.fastjson.JSONArray
import com.alibaba.fastjson.JSONObject

/**
 * @author  慕北_Innocent
 * @date  2022/2/12 13:12
 * @version 1.0
 */
class WeatherWarningConstructor(private val weatherWarningObject: JSONObject) {

    fun getWeatherWarning(): String {
        if (weatherWarningObject.containsKey("warning")) {
            val weatherWarningArray: JSONArray = weatherWarningObject["warning"] as JSONArray
            if (weatherWarningArray.size == 0) {
                return "气象灾害预警：当前区域无气象灾害预警"
            }
            var weatherWarningObj: JSONObject
            var res = "气象灾害预警：\n"
            var sender = ""
            var startTime = ""
            var endTime = ""
            for (cnt in weatherWarningArray.indices) {
                weatherWarningObj = weatherWarningArray[cnt] as JSONObject
                if (weatherWarningObj.containsKey("sender")) {
                    sender = weatherWarningObj["sender"] as String
                }
                var title = weatherWarningObj["title"] as String
                if (weatherWarningObj.containsKey("startTime")) {
                    startTime = weatherWarningObj["startTime"] as String
                }
                if (weatherWarningObj.containsKey("endTime")) {
                    endTime = weatherWarningObj["endTime"] as String
                }
                var level = weatherWarningObj["level"] as String
                var typeName = weatherWarningObj["typeName"] as String
                var text = weatherWarningObj["text"] as String

                res = "$res# 发布单位：$sender\n# 标题：$title\n# 开始时间：$startTime\n# 结束时间：$endTime\n# 预警等级：$level" +
                        "\n# 预警类型：$typeName\n# 详细信息：$text\n"
            }
            return res
        } else {
            return "当前区域暂无气象灾害预警信息"
        }
    }
}