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

fun Long.toBytes(): List<Byte> {
    return listOf(
        (this shr 56).toByte(),
        (this shr 48).toByte(),
        (this shr 40).toByte(),
        (this shr 32).toByte(),
        (this shr 24).toByte(),
        (this shr 16).toByte(),
        (this shr 8).toByte(),
        this.toByte()
    )
}

fun Int.toBytes(): List<Byte> {
    return listOf(
        (this shr 24).toByte(),
        (this shr 16).toByte(),
        (this shr 8).toByte(),
        this.toByte()
    )
}

fun Short.toBytes(): List<Byte> {
    return listOf(
        (this.toInt() shr 8).toByte(),
        this.toByte()
    )
}

fun Byte.toBytes(): List<Byte> {
    return listOf(this)
}

fun Double.toBytes(): List<Byte> {
    val long = this.toRawBits()
    return long.toBytes()
}
