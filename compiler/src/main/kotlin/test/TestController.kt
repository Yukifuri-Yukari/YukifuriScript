package yukifuri.script.compiler.test

import yukifuri.script.compiler.exception.Diagnostics
import yukifuri.script.compiler.lexer.Lexer
import yukifuri.script.compiler.lexer.util.CharStream
import yukifuri.script.compiler.lexer.util.CharStreamImpl
import yukifuri.script.compiler.parser.Parser
import yukifuri.script.compiler.visitor.bcgen.BytecodeGenerator
import yukifuri.script.compiler.visitor.bcgen.entry.Entry
import yukifuri.script.compiler.visitor.walker.Walker
import yukifuri.utils.colorama.Fore

class TestController {
    companion object {
        const val LOG = false
    }

    var mode = "WALK"

    var name: String = ""
    lateinit var text: List<String>
    lateinit var diagnostics: Diagnostics

    lateinit var cs: CharStream
    lateinit var lexer: Lexer
    lateinit var parser: Parser
    lateinit var walker: Walker

    var coloring = true

    fun printProgress(text: String, indent: Int = 8, ignoreLog: Boolean = false) {
        if (!LOG && !ignoreLog) return
        val s = "=".repeat(indent)
        if (coloring)
            println("${Fore.LIGHT_CYAN_EX}$s $text $s${Fore.RESET}")
        else println("$s $text $s")
    }

    fun log(obj: Any?) {
        if (!LOG) return
        println(obj)
    }

    fun setup(input: List<String>, filename: String) {
        name = filename
        text = input
        diagnostics = Diagnostics(filename, text)
        cs = CharStreamImpl(input.joinToString("\n"))
    }

    fun tryLexer() {
        cs = CharStreamImpl(text.joinToString("\n"))
        lexer = Lexer(cs, diagnostics)
        lexer.parse()
        printProgress("Lexer Result")
        lexer.tokens.forEach {
            log(it)
        }
    }

    fun tryParser() {
        parser = Parser(lexer.stream, diagnostics)
        parser.parse()
        printProgress("Parser Result")
        log(parser.file().module)
    }

    fun tryWalker() {
        if (mode == "WALK") {
            printProgress("Walking AST")
            printProgress("File: $name", 4, true)
            walker = Walker(parser.file())
            walker.exec()
            println()
        } else if (mode == "BCG") {
            tryBytecodeGeneration()
        }
    }

    fun tryBytecodeGeneration() {
        printProgress("Bytecode Generation")
        printProgress("File: $name", 4, true)
        val entry = Entry()
        entry.exec(parser.file())
        println("Symbol: ${entry.table.global}")
        printProgress("SymbolTable", 2)
        val bcGen = BytecodeGenerator()
        bcGen.exec(parser.file())
        println(bcGen.compiledFile())
        println(bcGen.toByteArray().toList())
    }

    fun test() {
        try {
            tryLexer()
            tryParser()
            tryWalker()
        } catch (e: Exception) {
            println(e.message ?: "Err: <no message>")
            e.printStackTrace()
        } finally {
            if (diagnostics.get().isNotEmpty()) {
                printProgress("Diagnostics")
            }
        }
    }
}
