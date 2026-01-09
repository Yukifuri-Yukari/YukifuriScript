package yukifuri.script.compiler

import yukifuri.script.compiler.exception.Diagnostics
import yukifuri.script.compiler.lexer.Lexer
import yukifuri.script.compiler.lexer.token.TokenStream
import yukifuri.script.compiler.lexer.util.CharStreamImpl
import yukifuri.script.compiler.parser.Parser
import yukifuri.script.compiler.walker.Walker
import yukifuri.utils.colorama.Fore
import java.io.File

val log = true

val file = File("test/HelloWorld.yuki")
val text = file
    .bufferedReader()
    .readLines()
val diagnostics = Diagnostics(file.name, text)

fun main() {
    try {
        test()
    } catch (e: Exception) {
        println(e.message)
        e.printStackTrace()
    } finally {
        if (diagnostics.get().isNotEmpty()) {
            printProgress("Diagnostics")
            diagnostics.print()
        }
    }
}

fun test() {
    val ts = tryLexer()
    tryParser(ts)
}

fun printProgress(text: String, indent: Int = 8) {
    if (!log) return
    val s = "=".repeat(indent)
    log("${Fore.LIGHT_CYAN_EX}$s $text $s${Fore.RESET}")
}

fun log(obj: Any?) {
    if (!log) return
    println(obj)
}

fun tryLexer(): TokenStream {
    val cs = CharStreamImpl(text.joinToString("\n"))
    val lexer = Lexer(cs, diagnostics)
    lexer.parse()
    printProgress("Lexer Result")
    lexer.tokens.forEach {
        log(it)
    }
    return lexer.stream
}

fun tryParser(ts: TokenStream) {
    val parser = Parser(ts, diagnostics)
    parser.parse()
    printProgress("Parser Result")
    log(parser.file().module)

    Walker(parser.file()).exec()
}
