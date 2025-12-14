package yukifuri.script.compiler.util

object Characters {
    fun valid(char: Char, list: List<Char> = Constants.chars) = char in list
}
