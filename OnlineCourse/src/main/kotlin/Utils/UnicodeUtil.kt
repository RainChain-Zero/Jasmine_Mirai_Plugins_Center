package rainchain.onlinecourse.Utils

import okhttp3.internal.toHexString

/**
 * @author  慕北_Innocent
 * @date  2022/3/12 19:54
 * @version 1.0
 */
class UnicodeUtil {

    fun Int.toHexString(): String = Integer.toHexString(this)

    //char ->unicode
    fun encode(char: Char) = "\\u${char.toInt().toHexString()}"

    //String ->unicode
    fun encode(text: String) = text
        .toCharArray()
        .map { encode(it) }
        .joinToString(separator = "", truncated = "")

    //unicode ->String
    fun decode(encodeText: String): String {
        fun decode1(unicode: String) = unicode.toInt(16).toChar()
        val unicodes = encodeText.split("\\u")
            .map { if (it.isNotBlank()) decode1(it) else null }.filterNotNull()
        return String(unicodes.toCharArray())
    }

}