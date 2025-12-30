package yukifuri.script.compiler

import yukifuri.script.compiler.exception.Diagnostics
import yukifuri.script.compiler.lexer.Lexer
import yukifuri.script.compiler.lexer.token.Token
import yukifuri.script.compiler.lexer.token.TokenStream
import yukifuri.script.compiler.lexer.token.TokenType
import yukifuri.script.compiler.lexer.util.CharStream
import yukifuri.script.compiler.lexer.util.CharStreamImpl
import yukifuri.script.compiler.parser.Parser
import yukifuri.script.compiler.walker.Walker
import yukifuri.utils.colorama.Fore
import java.io.File
import kotlin.math.log

val log = false;

fun main() {
    val file = File("test/HelloWorld.yuki")
    val text = file
        .bufferedReader()
        .readLines()
    val cs = CharStreamImpl(text.joinToString("\n"))
    val diagnostics = Diagnostics(file.canonicalPath, text)

    val ts = tryLexer(cs, diagnostics)
    tryParser(ts, diagnostics)

    diagnostics.print()
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

fun tryLexer(cs: CharStream, diagnostics: Diagnostics): TokenStream {
    val lexer = Lexer(cs, diagnostics)
    lexer.parse()
    printProgress("Lexer Result")
    lexer.tokens.forEach {
        log(it)
    }
    return lexer.stream
}

fun tryParser(ts: TokenStream, diagnostics: Diagnostics) {
    val parser = Parser(ts, diagnostics)
    parser.parse()
    printProgress("Parser Result")
    log(parser.getFile())

    Walker(parser.getFile()).exec()
}
