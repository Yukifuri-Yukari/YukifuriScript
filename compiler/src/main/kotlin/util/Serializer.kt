package yukifuri.script.compiler.util

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

object Serializer {
    fun serialize(str: String): String {
        val builder = StringBuilder()
        for (char in str) when (char) {
            '\\' -> builder.append("\\\\")
            '"' -> builder.append("\\\"")
            '\n' -> builder.append("\\n")
            '\r' -> builder.append("\\r")
            '\t' -> builder.append("\\t")
            '\b' -> builder.append("\\b")
            else -> builder.append(char)
        }
        return builder.toString()
    }

    fun deserialize(str: String): String {
        val builder = StringBuilder()
        var i = 0
        while (str.length > i) {
            when (val char = str[i++]) {
                '\\' -> {
                    when (str[i++]) {
                        '\\' -> builder.append('\\')
                        '"' -> builder.append('"')
                        'n' -> builder.append('\n')
                        'r' -> builder.append('\r')
                        't' -> builder.append('\t')
                        'b' -> builder.append('\b')
                        else -> throw Exception("Invalid escape character: $char")
                    }
                }
                else -> builder.append(char)
            }
        }
        return builder.toString()
    }

    fun time(
        time: LocalDateTime = LocalDateTime.now(),
        pattern: String = "yyyy-MM-dd HH:mm:ss"
    ): String {
        val formatter = DateTimeFormatter.ofPattern(pattern)
        return formatter.format(time)
    }
}
