package yukifuri.script.compiler.exception

import yukifuri.utils.colorama.Fore

class Diagnostics(
    val fileName: String,
    val source: List<String>
) {
    private val diagnostics = mutableListOf<Diagnostic>()

    fun add(diagnostic: Diagnostic) {
        diagnostics.add(diagnostic)
    }

    fun get(): List<Diagnostic> {
        return diagnostics
    }

    fun print() {
        if (diagnostics.isEmpty()) return
        println("----------")
        for (diagnostic in diagnostics) {
            print(if (diagnostic.level == Diagnostic.Level.Warning) Fore.YELLOW else Fore.RED)
            print("""
                ${diagnostic.level.name} at $fileName (${diagnostic.row + 1} : ${diagnostic.col + 1}):
                ${source[diagnostic.row]}
                ${" ".repeat(diagnostic.col)}^-This And After
                ${diagnostic.message}
                ----------
                """.trimIndent())
            println(Fore.RESET)
        }
    }
}
