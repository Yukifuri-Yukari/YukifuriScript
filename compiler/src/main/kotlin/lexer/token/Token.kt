package yukifuri.script.compiler.lexer.token

class Token(
    val type: TokenType,
    val text: String,
    val row: Int,
    val column: Int
) {
    override fun toString() =
        "Token(type=${type.name}, text=\"$text\", row=$row, column=$column)"

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Token) return false

        if (row != other.row) return false
        if (column != other.column) return false
        if (type != other.type) return false
        if (text != other.text) return false

        return true
    }

    override fun hashCode(): Int {
        var result = row
        result = 31 * result + column
        result = 31 * result + type.hashCode()
        result = 31 * result + text.hashCode()
        return result
    }
}
