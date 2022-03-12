package rainchain

import net.mamoe.mirai.console.plugin.jvm.JvmPluginDescription
import net.mamoe.mirai.console.plugin.jvm.KotlinPlugin
import net.mamoe.mirai.event.GlobalEventChannel
import net.mamoe.mirai.message.data.PlainText
import net.mamoe.mirai.utils.info
import Utils.OkhttpUtil
import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.JSONObject
import net.mamoe.mirai.event.events.MessageEvent
import net.mamoe.mirai.message.data.ForwardMessage
import rainchain.Utils.*
import java.text.DecimalFormat

object Plugin : KotlinPlugin(
    JvmPluginDescription(
        id = "rainchain.WeatherReport",
        name = "WeatherReport",
        version = "1.0",
    ) {
        author("RainChain")
    }
) {
    //API:https://console.qweather.com/#/apps

    //查询city经纬度的API-KEY
    private const val cityLocationKey: String = ""

    //查询七天天气所用的API-KEY
    private const val weatherDayKey: String = ""

    //查询24小时天气所用的API-KEY
    private const val weatherHourKey: String = ""

    //查询生活指数所需的API-KEY
    private const val lifeSuggestionKey: String = ""

    //查询空气质量的API-KEY
    private const val airConditionKey: String = ""

    //查询气象灾害的API-KEY
    private const val weatherWarningKey = ""

    //http工具类
    private val httpUtil: OkhttpUtil = OkhttpUtil()

    //目的地经纬度
    private var lat: String = ""
    private var lon: String = ""

    override fun onEnable() {
        logger.info { "WeatherReport Plugin loaded" }

        GlobalEventChannel.filter { event -> event is MessageEvent }
            .subscribeAlways<MessageEvent> { event ->
                val message=event.message.contentToString()
                if (message.startsWith("/w") || message.startsWith("/W")) {
                    //正则匹配提取参数
                    //!!注意kotlin的正则表达式中打小括号不会是多返回值的形式
                    val reg=Regex("(\\S+)")
                    val res=reg.findAll(message,2)
                    val loc:String
                    val adm:String
                    var array=Array<String>(2){""}
                    var cnt=0
                    res.forEach { array[cnt]=it.value;cnt++ }
                    loc=array[0]
                    adm=array[1]
                    //计算经纬度
                    val code=getLatAndLon(loc, adm)
                    if (code != "200") {
                        event.subject.sendMessage(PlainText("获取目的地失败，状态码：$code"))
                        if(code=="404"){
                            event.subject.sendMessage(PlainText("注：要求为“城市名 （上级行政区划）"))
                        }
                    } else {
                        //第一条消息，查询的地点
                        val location = "您查询的地点为$adm$loc"
                        //得到未来七天的天气数据
                        val dayWeatherArray: Array<String> = getDayWeatherArray(lon, lat)
                        //得到当日天气数据
                        val todayWeather = dayWeatherArray[0]
                        //得到当日生活指数
                        val lifeSuggesion = getTodayLifeSuggestion(lon, lat)
                        //得到未来24小时的天气数据
                        val hourWeatherArray: Array<String> = getHourWeatherArray(lon, lat)
                        //得到当日空气质量
                        val airCondition = getAirCondition(lon, lat)
                        //得到气象灾害预警
                        val weatherWarning = getWeatherWarning(lon, lat)
                        //构造消息，发送
                        event.subject.sendMessage(
                            messageOuterConstructor(
                                location, todayWeather,
                                "以下为未来六天的预报~", messageInner1Constructor(dayWeatherArray),
                                "以下为未来24小时的天气预报~", messageInner2Constructor(hourWeatherArray),
                                "以下为当日生活指数~", messageInner3Constructor(lifeSuggesion),
                                airCondition, weatherWarning
                            )
                        )
                    }
                }
            }
    }

    //计算目的地经纬度
    fun getLatAndLon(loc: String, adm: String):String {
        //得到含有经纬度的json1
        val locationJsonStr =
            httpUtil.run("https://geoapi.qweather.com/v2/city/lookup?location=$loc&adm=$adm&key=$cityLocationKey&num=1")
        val locationJsonObject: JSONObject = JSON.parseObject(locationJsonStr)
        //计算经纬度
        val locationObj = LocationJsonUtils(locationJsonObject)

        val code=locationObj.getLocattion()
        if(code!="200")
        {
            return code
        }
        //全部保留小数点后两位
        this.lat = DecimalFormat("#.##").format(locationObj.getLat().toDouble()).toString() //纬度
        this.lon = DecimalFormat("#.##").format(locationObj.getLon().toDouble()).toString()  //经度
        return "200"
    }

    //提取生活指数  type=1 2 3 4 5 7 8 9 11 14 15 16
    fun getTodayLifeSuggestion(lon: String, lat: String): String {

        //得到含有生活指数的json
        val lifeSuggestionJsonStr: String = httpUtil.run(
            "https://devapi.qweather.com/v7/indices/1d?location=$lon,$lat" +
                    "&type=1,2,3,4,5,7,8,9,11,14,15,16&key=$lifeSuggestionKey"
        )
        val lifeSuggestionJSONObject: JSONObject = JSON.parseObject(lifeSuggestionJsonStr)

        if (lifeSuggestionJSONObject["code"] != "200") {
            return "获取当天天气指数失败，状态码：" + lifeSuggestionJSONObject["code"]
        }

        val lifeSuggestionObj = LifeSuggestionJsonUtils(lifeSuggestionJSONObject)
        return lifeSuggestionObj.getLifeSuggestion()
    }

    //得到未来七天的天气数据，经度在前，维度在后
    fun getDayWeatherArray(lon: String, lat: String): Array<String> {


        val dayWeatherJsonStr = httpUtil.run(
            "https://devapi.qweather.com/v7/weather/7d?location=$lon,$lat&key=$weatherDayKey"
        )
        val dayWeatherJsonObject = JSON.parseObject(dayWeatherJsonStr)
        if (dayWeatherJsonObject["code"] != "200") {
            return arrayOf("未来七天天气数据获取失败，状态码：" + dayWeatherJsonObject["code"])
        }
        val dayWeatherObj = DayWeatherConstructor(dayWeatherJsonObject)
        return dayWeatherObj.getDayWeatherArray()
    }

    //得到未来24小时的数据
    fun getHourWeatherArray(lon: String, lat: String): Array<String> {

        val hourWeatherJsonStr =
            httpUtil.run("https://devapi.qweather.com/v7/weather/24h?location=$lon,$lat&key=$weatherHourKey")
        val hourWeatherJsonObject = JSON.parseObject(hourWeatherJsonStr)
        if (hourWeatherJsonObject["code"] != "200") {
            return arrayOf("未来24小时天气数据获取失败，状态码：" + hourWeatherJsonObject["code"])
        }
        val hourWeatherObj = HourWeatherConstructor(hourWeatherJsonObject)
        return hourWeatherObj.getHourWeather()
    }

    //得到空气质量数据
    fun getAirCondition(lon: String, lat: String): String {
        val airConditionJsonStr = httpUtil.run(
            "https://devapi.qweather.com/v7/air/now?location=$lon,$lat&" +
                    "key=$airConditionKey"
        )
        val airCoditionJsonObject = JSON.parseObject(airConditionJsonStr)
        if (airCoditionJsonObject["code"] != "200") {
            return "空气质量获取失败，状态码：" + airCoditionJsonObject["code"]
        }
        val airConditionObject = AirConditionConstructor(airCoditionJsonObject)
        return airConditionObject.getAirCondition()
    }

    //得到天气预警信息
    fun getWeatherWarning(lon: String, lat: String): String {
        val weatherWarningJsonStr = httpUtil.run(
            "https://devapi.qweather.com/v7/warning/now?location=$lon,$lat&" +
                    "key=$weatherWarningKey"
        )
        val weatherWarningObject = JSON.parseObject(weatherWarningJsonStr)
        if (weatherWarningObject["code"] != "200") {
            return "气象灾害信息获取失败，状态码：" + weatherWarningObject["code"]
        }
        val weatherWarningObj = WeatherWarningConstructor(weatherWarningObject)
        return weatherWarningObj.getWeatherWarning()
    }

    //最外层转发消息构造
    fun messageOuterConstructor(
        content1: String,
        content2: String,
        content3: String,
        content4: ForwardMessage,
        content5: String,
        content6: ForwardMessage,
        content7: String,
        content8: ForwardMessage,
        content9: String,
        content10: String
    ): ForwardMessage {
        val node1 = ForwardMessage.Node(3349795206L, 0, "茉莉", PlainText(content1))
        val node2 = ForwardMessage.Node(3349795206L, 0, "茉莉", PlainText(content2))
        val node3 = ForwardMessage.Node(3349795206L, 0, "茉莉", PlainText(content3))
        val node4 = ForwardMessage.Node(3349795206L, 0, "茉莉", content4)
        val node5 = ForwardMessage.Node(3349795206L, 0, "茉莉", PlainText(content5))
        val node6 = ForwardMessage.Node(3349795206L, 0, "茉莉", content6)
        val node7 = ForwardMessage.Node(3349795206L, 0, "茉莉", PlainText(content7))
        val node8 = ForwardMessage.Node(3349795206L, 0, "茉莉", content8)
        val node9 = ForwardMessage.Node(3349795206L, 0, "茉莉", PlainText(content9))
        val node10 = ForwardMessage.Node(3349795206L, 0, "茉莉", PlainText(content10))

        val msgOuter =
            listOf<ForwardMessage.Node>(node1, node2, node3, node4, node5, node6, node7, node8, node9, node10)
        return ForwardMessage(listOf("为什么不点开看看呢?"), "查询结果", "天气查询结果", "聊天记录", "天气查询结果", msgOuter)
    }

    //未来六天转发消息构造
    fun messageInner1Constructor(content: Array<String>): ForwardMessage {
        //创建6个结点list
        var listOfNode = List<ForwardMessage.Node>(6) {
            ForwardMessage.Node(3349795206L, 0, "茉莉", PlainText(""))
        }
            .toMutableList()
        for (cnt in 0..content.size - 2) {
            listOfNode[cnt] = ForwardMessage.Node(3349795206L, 0, "茉莉", PlainText(content[cnt + 1]))
        }
        return ForwardMessage(listOf("这是未来6天的天气数据~"), "查询结果", "天气查询结果", "聊天记录", "未来六天天气", listOfNode)
    }

    //24小时天气转发消息构造
    fun messageInner2Constructor(content: Array<String>): ForwardMessage {
        //创建24个结点list
        var listOfNode = List<ForwardMessage.Node>(24) {
            ForwardMessage.Node(3349795206L, 0, "茉莉", PlainText(""))
        }
            .toMutableList()
        for (cnt in content.indices) {
            listOfNode[cnt] = ForwardMessage.Node(3349795206L, 0, "茉莉", PlainText(content[cnt]))
        }
        return ForwardMessage(listOf("这是未来24小时的天气数据~"), "查询结果", "天气查询结果", "聊天记录", "未来24小时天气", listOfNode)
    }

    //生活指数转发消息构造
    fun messageInner3Constructor(content: String): ForwardMessage {
        val node = ForwardMessage.Node(3349795206L, 0, "茉莉", PlainText(content))
        return ForwardMessage(
            listOf("这是当天的生活指数~"), "查询结果", "生活指数查询结果", "聊天记录", "当日生活指数",
            listOf<ForwardMessage.Node>(node)
        )
    }

}