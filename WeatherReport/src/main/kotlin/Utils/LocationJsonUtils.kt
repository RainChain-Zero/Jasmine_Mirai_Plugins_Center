package rainchain.Utils

import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.JSONArray
import com.alibaba.fastjson.JSONObject
import kotlinx.serialization.json.Json
import java.util.logging.Logger
import kotlin.reflect.jvm.internal.impl.load.kotlin.JvmType

/**
 * @author  慕北_Innocent
 * @date  2022/2/11 17:33
 * @version 1.0
 */
class LocationJsonUtils(private val locationJsonObject: JSONObject) {
    //得到经纬度坐标
    //默认为-1
    private var lat: String = ""
    private var lon: String = ""
    private var code: String = ""
    fun getLocattion(): String {
        if (locationJsonObject["code"] != "200") {
            return locationJsonObject["code"] as String
        }
        val locationArray = locationJsonObject["location"] as JSONArray
        val jsonobject = locationArray[0] as JSONObject
        lat = jsonobject["lat"] as String
        lon = jsonobject["lon"] as String
        code = locationJsonObject["code"] as String
        return "200"
    }

    fun getLat(): String {
        return lat
    }

    fun getLon(): String {
        return lon
    }

    fun getCode(): String {
        return code
    }
}
