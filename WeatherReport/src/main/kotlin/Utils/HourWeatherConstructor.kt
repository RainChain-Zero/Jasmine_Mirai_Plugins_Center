package rainchain.Utils

import com.alibaba.fastjson.JSONArray
import com.alibaba.fastjson.JSONObject

/**
 * @author  慕北_Innocent
 * @date  2022/2/12 1:42
 * @version 1.0
 */
class HourWeatherConstructor(private val hourWeatherJsonObject: JSONObject) {
    //得到24小时数据的结果数组
    fun getHourWeather(): Array<String> {
        //数据变量
        var code = hourWeatherJsonObject["code"] as String
        var fxTime = ""  //预报时间
        var temp = ""  //温度
        var icon = ""  //天气状况和图标的代码
        var text = ""  //天气状况的文字描述
        var windDir = ""  //风向
        var windScale = ""  //风力等级
        var humidity = ""  //相对湿度，百分比数值
        var precip = ""  //当前小时累计降水量，默认单位：毫米
        var pop = ""  //逐小时预报降水概率，百分比数值，可能为空

        var hourWeatherJsonArray: JSONArray = hourWeatherJsonObject["hourly"] as JSONArray
        var hourWeatherArray = Array<String>(24) { "" }
        var hourWeatherObj: JSONObject

        for (cnt in 0..23) {
            hourWeatherObj = hourWeatherJsonArray[cnt] as JSONObject

            //获取数据
            fxTime = hourWeatherObj["fxTime"] as String
            temp = hourWeatherObj["temp"] as String
            icon = hourWeatherObj["icon"] as String
            text = hourWeatherObj["text"] as String
            windDir = hourWeatherObj["windDir"] as String
            windScale = hourWeatherObj["windScale"] as String
            humidity = hourWeatherObj["humidity"] as String
            precip = hourWeatherObj["precip"] as String
            pop = hourWeatherObj["pop"] as String

            hourWeatherArray[cnt] = "预报时间：$fxTime\n# 温度：$temp℃\n# 天气：$text\n# 风向：$windDir；风力：$windScale\n" +
                    "# 相对湿度：$humidity%\n# 预计当前小时降水量（mm）：$precip\n# 降水概率：$pop%"
        }
        return hourWeatherArray
    }
}