package yukifuri.script.compiler.util

object Constants {
    val chars = ('a' .. 'z') + ('A' .. 'Z') + '_'

    val numbers = ('0' .. '9')

    val charWithNumber = chars + numbers

    val hexNumbers = ('0' .. '9') + ('a' .. 'f') + ('A' .. 'F')

    val octNumbers = ('0' .. '7')

    val binNumbers = ('0' .. '1')

    val keywords = listOf(
        "function"
    )
}
