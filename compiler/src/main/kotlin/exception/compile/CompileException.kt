package yukifuri.script.compiler.exception.compile

open class CompileException(
    message: String = "Unnamed Exception(CompileException)",
    val procedure: String = "None",
) : Exception(message)
