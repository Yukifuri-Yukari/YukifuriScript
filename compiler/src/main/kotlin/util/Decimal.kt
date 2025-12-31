package yukifuri.script.compiler.util

fun toInt(s: String): Int {
    val lower = s.lowercase()
    if (lower.startsWith("0x"))
        return lower.substring(2).toInt(16)
    if (lower.startsWith("0b"))
        return lower.substring(2).toInt(2)
    if (lower.startsWith("0o"))
        return lower.substring(2).toInt(8)
    return lower.toInt()
}
