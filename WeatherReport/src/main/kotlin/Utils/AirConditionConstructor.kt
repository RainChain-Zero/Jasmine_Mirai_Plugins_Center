package rainchain.Utils

import com.alibaba.fastjson.JSONObject

/**
 * @author  慕北_Innocent
 * @date  2022/2/12 12:41
 * @version 1.0
 */
class AirConditionConstructor(private val airConditionJsonObject: JSONObject) {

    fun getAirCondition(): String {

        val airConditionObj: JSONObject = airConditionJsonObject["now"] as JSONObject

        var pubTime = airConditionObj["pubTime"] as String  //发布时间
        var aqi = airConditionObj["aqi"] as String  //空气质量指数
        var category = airConditionObj["category"] as String  //空气质量指数级别
        var primary = airConditionObj["primary"] as String  //空气质量的主要污染物，空气质量为优时，返回值为NA
        var pm10 = airConditionObj["pm10"] as String  //PM10
        var pm2p5 = airConditionObj["pm2p5"] as String  //pm2p5
        var no2 = airConditionObj["no2"] as String  //二氧化氮
        var so2 = airConditionObj["so2"] as String  //二氧化硫
        var co = airConditionObj["co"] as String  // 一氧化碳
        var o3 = airConditionObj["o3"] as String  //臭氧

        return "当前空气质量：\n# 发布时间：$pubTime\n# 空气质量指数：$aqi\n# 空气质量：$category\n# 空气主要污染物：$primary\n" +
                "# PM10：$pm10\n# PM2.5：$pm2p5\n# NO2：$no2\n# SO2：$so2\n# CO：$co\n# O3：$o3"
    }
}