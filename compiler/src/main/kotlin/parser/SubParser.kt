package yukifuri.script.compiler.parser

import yukifuri.script.compiler.exception.Diagnostic

abstract class SubParser(
    val self: Parser
) {
    /* Util */
    protected fun peek() = self.peek()
    protected fun next() = self.next()
    protected fun addDiagnostic(message: String, level: Diagnostic.Level = Diagnostic.Level.Error) {
        self.addDiagnostic(message, level)
    }
    protected fun addAndError(message: String) {
        self.addAndError(message)
    }
    protected val ts = self.ts
    protected val diagnostics by lazy {
        self.diagnostics
    }
    protected val expr by lazy {
        self.expr
    }
    protected val stmt by lazy {
        self.stmt
    }
    protected val topLevel by lazy {
        self.topLevel
    }
}
