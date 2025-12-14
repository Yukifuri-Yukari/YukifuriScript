package yukifuri.script.compiler.exception.compile

class EOFException(
    message: String,
    procedure: String = "None"
) : CompileException(message, procedure)
