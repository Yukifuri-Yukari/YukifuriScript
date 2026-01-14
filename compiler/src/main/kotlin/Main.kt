package yukifuri.script.compiler

import yukifuri.script.compiler.ast.structure.YFile
import yukifuri.script.compiler.exception.Diagnostics
import yukifuri.script.compiler.lexer.Lexer
import yukifuri.script.compiler.lexer.token.TokenStream
import yukifuri.script.compiler.lexer.util.CharStreamImpl
import yukifuri.script.compiler.parser.Parser
import yukifuri.script.compiler.walker.Walker
import yukifuri.utils.colorama.Fore
import java.io.File

const val LOG = true

lateinit var file: File
lateinit var text: List<String>
lateinit var diagnostics: Diagnostics

lateinit var args: Array<String>

fun setup() {
    file = File(args[0])
    text = file
        .bufferedReader()
        .readLines()
    diagnostics = Diagnostics(file.name, text)
}

fun main(argsIn: Array<String>) {
    args = argsIn
    setup()
    try {
        test()
    } catch (e: Exception) {
        println(e.message ?: "Err: <no message>")
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
    val parser = tryParser(ts)
    tryWalker(parser.file())
}

fun printProgress(text: String, indent: Int = 8) {
    if (!LOG) return
    val s = "=".repeat(indent)
    log("${Fore.LIGHT_CYAN_EX}$s $text $s${Fore.RESET}")
}

fun log(obj: Any?) {
    if (!LOG) return
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

fun tryParser(ts: TokenStream): Parser {
    val parser = Parser(ts, diagnostics)
    parser.parse()
    printProgress("Parser Result")
    log(parser.file().module)

    return parser
}

fun tryWalker(file: YFile) {
    printProgress("Walking AST")
    val walker = Walker(file)
    walker.exec()
}
