package yukifuri.script.compiler.lexer.token

class Token(
    val type: TokenType,
    val text: String,
    val row: Int,
    val column: Int
) {
    override fun toString() =
        "Token(type=${type.name}, text=\"$text\", row=$row, column=$column)"

    override fun hashCode(): Int {
        var result = type.hashCode()
        result = 31 * result + text.hashCode()
        return result
    }

    override operator fun equals(other: Any?): Boolean {
        if (other is Pair<*, *> && other.first is TokenType && other.second is String) {
            return type == other.first && (text == other.second || (other.second as String).trim().isEmpty())
        }

        if (other is TokenType) {
            return type == other
        }

        if (other is String) {
            return text == other
        }

        return other is Token && type == other.type && text == other.text
    }
}
