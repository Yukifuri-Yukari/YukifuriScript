package yukifuri.script.compiler

import yukifuri.script.compiler.exception.Diagnostics
import yukifuri.script.compiler.exception.compile.CompileException
import yukifuri.script.compiler.lexer.Lexer
import yukifuri.script.compiler.lexer.util.CharStream
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

    try {
        tryLexer(cs, diagnostics)
    } catch (ce: CompileException) {
        println(ce)
        diagnostics.print()
        println("Parsing Progress terminated with abnormal status.")
        return
    }
    diagnostics.print()
}

fun printProgress(text: String) {
    println("${Fore.LIGHT_CYAN_EX}======== $text ========${Fore.RESET}")
}

fun tryLexer(cs: CharStream, diagnostics: Diagnostics) {
    val lexer = Lexer(cs, diagnostics)
    lexer.parse()
    printProgress("Lexer Result")
    lexer.tokens.forEach {
        println(it)
    }
}
