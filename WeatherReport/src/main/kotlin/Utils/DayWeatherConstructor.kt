package rainchain.Utils

import com.alibaba.fastjson.JSONArray
import com.alibaba.fastjson.JSONObject

/**
 * @author  慕北_Innocent
 * @date  2022/2/11 21:49
 * @version 1.0
 */
class DayWeatherConstructor(private val dayWeatherJsonObject: JSONObject) {

    fun getDayWeatherArray(): Array<String> {
        //数据变量
        var code = dayWeatherJsonObject["code"] as String
        var fxDate = ""  //播报日期
        var tempMin = ""  //最低温
        var tempMax = ""  //最高温
        var iconDay = ""   //白天天气状况图标
        var textDay = ""  //白天天气
        var iconNight = ""  //夜晚天气状况图标
        var textNight = ""  //夜晚天气状态
        var windDirDay = ""  //白天风向
        var windScaleDay = ""  //白天风力等级
        var windDirNight = ""  //夜晚风向
        var windScaleNight = "" //夜晚风力等级
        var precip = ""   //预计当天总降水量 mm
        var uvIndex = ""   //紫外线指数
        var humidity = ""  //相对湿度  %
        var vis = ""    //能见度  公里

        //得到JSONObject数组
        var dayWeatherJsonArray: JSONArray = dayWeatherJsonObject["daily"] as JSONArray
        var dayWeatherArray = Array<String>(7) { "" }
        var dayWeatherObj: JSONObject
        //循环遍历七天的天气
        for (cnt in 0..6) {
            dayWeatherObj = dayWeatherJsonArray[cnt] as JSONObject

            fxDate = dayWeatherObj["fxDate"] as String
            tempMin = dayWeatherObj["tempMin"] as String
            tempMax = dayWeatherObj["tempMax"] as String
            iconDay = dayWeatherObj["iconDay"] as String
            textDay = dayWeatherObj["textDay"] as String
            iconNight = dayWeatherObj["iconNight"] as String
            textNight = dayWeatherObj["textNight"] as String
            windDirDay = dayWeatherObj["windDirDay"] as String
            windScaleDay = dayWeatherObj["windScaleDay"] as String
            windDirNight = dayWeatherObj["windDirNight"] as String
            windScaleNight = dayWeatherObj["windScaleNight"] as String
            precip = dayWeatherObj["precip"] as String
            uvIndex = dayWeatherObj["uvIndex"] as String
            humidity = dayWeatherObj["humidity"] as String
            vis = dayWeatherObj["vis"] as String

            dayWeatherArray[cnt] = fxDate + "\n# 温度：" + tempMin + "℃~" + tempMax + "℃\n" +
                    "# 日间天气：" + textDay + "；夜间天气：" + textNight +
                    "\n# 日间风向：" + windDirDay + "，风级：" + windScaleDay + "；夜间风向：" + windDirNight + "，风级：" + windScaleNight +
                    "\n# 预计当前总降水量(mm)：" + precip + "\n# 相对湿度：" + humidity +
                    "%\n# 紫外线指数：" + uvIndex + "\n# 能见度(km)：" + vis
        }
        return dayWeatherArray
    }
}