package Utils

import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException

/**
 * @author 慕北_Innocent
 * @version 1.0
 * @date 2022/2/11 12:57
 */
class OkhttpUtil {
    val client = OkHttpClient()

    @Throws(IOException::class)
    fun run(url: String): String {
        val request: Request = Request.Builder()
            .url(url)
            .build()
        try {
            client.newCall(request).execute().use { response -> return response.body!!.string() }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return "请求失败！"
    }
}