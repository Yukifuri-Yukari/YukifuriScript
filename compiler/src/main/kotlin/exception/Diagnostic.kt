package yukifuri.script.compiler.exception

class Diagnostic private constructor(
    val level: Level,
    val row: Int,
    val col: Int,
    val position: Int,
    val message: String
) {
    enum class Level {
        Warning,
        Error,
    }
    companion object {
        fun of(row: Int, col: Int, position: Int, message: String, level: Level = Level.Warning): Diagnostic {
            return Diagnostic(level, row, col, position, message)
        }
    }

    override fun toString(): String {
        return "${level.name} ($row, $col): $message"
    }
}
