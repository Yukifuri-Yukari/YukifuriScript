package yukifuri.script.compiler

import yukifuri.script.compiler.ast.structure.YFile
import yukifuri.script.compiler.exception.Diagnostics
import yukifuri.script.compiler.lexer.Lexer
import yukifuri.script.compiler.lexer.token.TokenStream
import yukifuri.script.compiler.lexer.util.CharStreamImpl
import yukifuri.script.compiler.parser.Parser
import yukifuri.script.compiler.util.Serializer.time
import yukifuri.script.compiler.walker.Walker
import yukifuri.utils.colorama.Fore
import java.io.File
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import kotlin.emptyArray
import kotlin.time.Instant

const val LOG = false

var name: String = ""
lateinit var text: List<String>
lateinit var diagnostics: Diagnostics

fun setup(input: File) {
    val reader = input.bufferedReader()
    val lastEdited = input.lastModified()
    val time = LocalDateTime.ofInstant(
        java.time.Instant.ofEpochMilli(lastEdited),
        ZoneId.systemDefault()
    )
    text = reader.readLines()
    reader.close()
    name = input.name + " ${time(time)}"
    diagnostics = Diagnostics(name, text)
}

fun setup(input: String) {
    text = input.lines()
    name = "<stdin> ${time()}"
    diagnostics = Diagnostics(name, text)
}

fun main(args: Array<String>) {
    if (args.isEmpty()) {
        while (true) {
            print(" >>>")
            val input = readln()
            setup(input)

            if (input.isEmpty()) continue
            if (input == ":exit") return
            if (input.startsWith(":load ")) {
                val file = File(input
                    .substringAfter(":load ")
                )
                setup(file)
            }

            try {
                test()
            } catch (e: Exception) {
                println(e.message ?: "Err: <no message>")
                e.printStackTrace()
            }
        }
    }

    val files = when {
        args.size > 1 -> args.map { File(it) }
        args[0].endsWith("*") -> {
            val dir = File(args[0].substringBeforeLast("*"))
            dir.listFiles()!!.toList().also { println(it) }
        }
        else -> listOf(File(args[0]))
    }
    for (f in files) {
        setup(f)
        test()
    }
}

fun test() {
    try {
        val ts = tryLexer()
        val parser = tryParser(ts)
        tryWalker(parser.file())
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

fun printProgress(text: String, indent: Int = 8) {
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
    println("${Fore.LIGHT_CYAN_EX}==== File: $name ====${Fore.RESET}")
    val walker = Walker(file)
    walker.exec()
    println()
}
