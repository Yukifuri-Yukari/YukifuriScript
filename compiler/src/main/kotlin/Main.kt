package yukifuri.script.compiler

import yukifuri.script.compiler.exception.Diagnostics
import yukifuri.script.compiler.lexer.Lexer
import yukifuri.script.compiler.lexer.token.TokenStream
import yukifuri.script.compiler.lexer.token.TokenType
import yukifuri.script.compiler.lexer.util.CharStream
import yukifuri.script.compiler.lexer.util.CharStreamImpl
import yukifuri.script.compiler.parser.Parser
import yukifuri.script.compiler.walker.Walker
import yukifuri.utils.colorama.Fore
import java.io.File

val log = true

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

    /*val p = Parser(TokenStream( // ((1 + 2) * 3 / 4) * 5 - 6 + 7 * 8
        TokenType.LParen to "(",
        TokenType.LParen to "(",
        TokenType.Integer to "1",
        TokenType.Operator to "+",
        TokenType.Integer to "2",
        TokenType.Operator to "*",
        TokenType.Integer to "3",
        TokenType.Operator to "/",
        TokenType.Integer to "4",
        TokenType.RParen to ")",
        TokenType.Operator to "*",
        TokenType.Integer to "5",
        TokenType.Operator to "-",
        TokenType.Integer to "6",
        TokenType.RParen to ")",
        TokenType.Operator to "+",
        TokenType.Integer to "7",
        TokenType.Operator to "*",
        TokenType.Integer to "8",
        TokenType.EOF to "",
    ), diagnostics)

    p.parse()

    println(p.getFile())*/

    Walker(parser.getFile()).exec()
}
