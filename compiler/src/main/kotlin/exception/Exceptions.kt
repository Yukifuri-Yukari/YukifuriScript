package yukifuri.script.compiler.exception

import yukifuri.script.compiler.exception.compile.CompileException
import yukifuri.script.compiler.exception.compile.EOFException

fun throwEOF(message: String = "File reached EOF") {
    throw EOFException(message)
}

fun throwCE(message: String = "Compile Error") {
    throw CompileException(message)
}
