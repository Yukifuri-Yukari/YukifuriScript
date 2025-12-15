package yukifuri.script.compiler

import yukifuri.script.compiler.exception.Diagnostics
import yukifuri.script.compiler.lexer.Lexer
import yukifuri.script.compiler.lexer.util.CharStreamImpl
import yukifuri.utils.colorama.Fore
import java.io.File

fun main() {
    val file = File("test/HelloWorld.yuki")
    val text = file
        .bufferedReader()
        .readLines()
    val cs = CharStreamImpl(text.joinToString("\n"))
    val diagnostics = Diagnostics(file.canonicalPath, text)

    val lexer = Lexer(cs, diagnostics)
    lexer.parse()
    println("${Fore.LIGHT_CYAN_EX}======== Lexer Result ========${Fore.RESET}")
    lexer.tokens.forEach {
        println(it)
    }
    // println("${Fore.LIGHT_CYAN_EX}======== Parser Result ========${Fore.RESET}")
    diagnostics.print()
}