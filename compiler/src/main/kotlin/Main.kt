package yukifuri.script.compiler

import yukifuri.script.compiler.exception.Diagnostics
import yukifuri.script.compiler.lexer.Lexer
import yukifuri.script.compiler.lexer.token.Token
import yukifuri.script.compiler.lexer.token.TokenStream
import yukifuri.script.compiler.lexer.token.TokenType
import yukifuri.script.compiler.lexer.util.CharStream
import yukifuri.script.compiler.lexer.util.CharStreamImpl
import yukifuri.script.compiler.parser.Parser
import yukifuri.script.compiler.walker.Walker
import yukifuri.utils.colorama.Fore
import java.io.File

fun main() {
    val file = File("test/HelloWorld.yuki")
    val text = file
        .bufferedReader()
        .readLines()
    val cs = CharStreamImpl(text.joinToString("\n"))
    val diagnostics = Diagnostics(file.canonicalPath, text)

    // tryLexer(cs, diagnostics)

    /*
File(
  module=Module(
    statements=[
      Function(
        name=hello,
        args=[],
        return=Nothing,
        body=Module(
        statements=[
          FunctionCall(name='print', args=[
            StringLiteral(value="Hello World!")
          ])
        ]
      )
    ),
    Function(
      name=main,
      args=[],
      return=Nothing,
      body=Module(
        statements=[
          FunctionCall(name='hello', args=[])
        ]
      )
    )
  ]
)
    */
    tryParser(TokenStream(
        TokenType.Keyword to "function",
        TokenType.Identifier to "hello",
        TokenType.LParen to "(",
        TokenType.RParen to ")",
        TokenType.LBrace to "{",
        TokenType.Identifier to "print",
        TokenType.LParen to "(",
        TokenType.StringLiteral to "\"Hello World!\"",
        TokenType.RParen to ")",
        TokenType.Semicolon to ";",
        TokenType.RBrace to "}",

        TokenType.Keyword to "function",
        TokenType.Identifier to "main",
        TokenType.LParen to "(",
        TokenType.RParen to ")",
        TokenType.LBrace to "{",
        TokenType.Identifier to "hello",
        TokenType.LParen to "(",
        TokenType.RParen to ")",
        TokenType.Semicolon to ";",
        TokenType.RBrace to "}",

        TokenType.EOF to ""
    ), diagnostics)

    diagnostics.print()
}

fun printProgress(text: String, indent: Int = 8) {
    val s = "=".repeat(indent)
    println("${Fore.LIGHT_CYAN_EX}$s $text $s${Fore.RESET}")
}

fun tryLexer(cs: CharStream, diagnostics: Diagnostics) {
    val lexer = Lexer(cs, diagnostics)
    lexer.parse()
    printProgress("Lexer Result")
    lexer.tokens.forEach {
        println(it)
    }
}

fun tryParser(ts: TokenStream, diagnostics: Diagnostics) {
    val parser = Parser(ts, diagnostics)
    parser.parse()
    printProgress("Parser Result")
    println(parser.getFile())

    printProgress("Environment", 4)
    println(parser.getTable())

    Walker(parser.getFile()).exec()
}
