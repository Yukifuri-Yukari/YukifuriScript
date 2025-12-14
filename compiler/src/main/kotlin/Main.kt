package yukifuri.script.compiler

import yukifuri.script.compiler.lexer.Lexer
import yukifuri.script.compiler.lexer.util.CharStreamImpl
import java.io.File

fun main() {
    val cs = CharStreamImpl(File("test/HelloWorld.yuki")
        .bufferedReader()
        .readText()
    )
    val lexer = Lexer(cs)
    lexer.parse()
}