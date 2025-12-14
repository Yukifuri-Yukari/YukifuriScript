package yukifuri.script.compiler.exception

import yukifuri.script.compiler.exception.compile.EOFException

fun throwEOF(message: String = "File reached EOF") {
    throw EOFException(message)
}
