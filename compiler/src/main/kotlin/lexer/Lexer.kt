package yukifuri.script.compiler.lexer

import yukifuri.script.compiler.exception.Diagnostic
import yukifuri.script.compiler.exception.Diagnostics
import yukifuri.script.compiler.exception.throwCE
import yukifuri.script.compiler.lexer.token.Token
import yukifuri.script.compiler.lexer.token.TokenStream
import yukifuri.script.compiler.lexer.token.TokenType
import yukifuri.script.compiler.lexer.util.CharStream
import yukifuri.script.compiler.util.Const

class Lexer(
    private val cs: CharStream,
    private val diagnostics: Diagnostics
) {
    companion object {
        var outputEOL = false
    }
    val tokens = mutableListOf<Token>()
    var stream = TokenStream(listOf())

    /* Util Functions */
    fun next() = cs.next()
    fun peek() = cs.peek()
    fun current() = cs.peek()
    fun eof() = cs.eof()
    fun next(n: Int = 1) = cs.next(n)
    fun peek(n: Int = 1) = cs.peek(n)
    private fun addDiagnostic(message: String) {
        diagnostics.add(
            Diagnostic.of(
                cs.currentRow(),
                cs.currentCol(),
                cs.position(),
                message
            )
        )
    }
    private fun emit(type: TokenType, text: String = "") {
        val tk = Token(type, text, cs.row(), cs.col())
        tokens.add(tk)
        cs.updatePosition()
    }

    /* Parsing Functions */
    private fun skipWhiteSpace(): Boolean {
        var executed = false
        while (!eof() && current() in Const.whitespaces) {
            if (next() == '\n' && outputEOL) emit(TokenType.EOL, "")
            executed = true
        }
        return executed
    }

    private fun skipComment(): Boolean {
        if (current() != '/') return false
        when (peek(2)) {
            "//" -> {
                next(2)
                while (!eof() && current() != '\n') {
                    next()
                }
            }

            "/*" -> {
                next(2)
                var domain = 1
                while (!eof() && domain > 0) {
                    if (peek(2) == "/*") {
                        domain++
                        next(2)
                    }
                    if (peek(2) == "*/") {
                        domain--
                        next(2)
                    }
                    next()
                }
            }
            else -> return false
        }
        return true
    }

    private fun parseIdentifierAndKeyword() {
        if (current() !in Const.chars) return
        val builder = StringBuilder()
        while (!eof() && current() in Const.charWithNumber) {
            builder.append(next())
        }
        val s = builder.toString()
        emit(
            if (s in Const.keywords) TokenType.Keyword
            else TokenType.Identifier,
            s
        )
    }

    private fun parseString() {
        if (current() != '\"') return
        val isMultiline = peek(3) == "\"\"\""
        val builder = StringBuilder()
        if (isMultiline) next(3) else next()
        while (!eof()) {
            if (isMultiline && peek(3) == "\"\"\"") next(3).also { break }
            if (current() == '\\') {
                next()
                if (eof()) addDiagnostic(
                    "Invalid escape sequence cause of incompleted string."
                ).also { throwCE("Compile Error") }
                if (current() == '\"') {
                    builder.append("\\\"")
                    next()
                }
            }
            if (current() == '\"') next().also { break }
            builder.append(next())
        }
    }

    private fun parseFloatings() {}

    private fun collect(set: Set<Char>): String {
        val builder = StringBuilder()
        while (!eof() && current() in set) {
            builder.append(next())
        }
        return builder.toString()
    }

    private fun parseNumbers() {
        if (current() !in Const.validNumbers) return
        if (current() == '0') when (val possiblePrefix = peek()) {
            'x' -> emit(
                TokenType.Integer,
                "0${possiblePrefix}${collect(Const.hexNumbers)}"
            )
            'o' -> emit(
                TokenType.Integer,
                "0${possiblePrefix}${collect(Const.octNumbers)}"
            )
            'b' -> emit(
                TokenType.Integer,
                "0${possiblePrefix}${collect(Const.binNumbers)}"
            )
            '.' -> return parseFloatings()
        }
        if (current() == '.') return parseFloatings()
    }

    private fun parseSimpleTokens() {
        when (current()) {
            in "()[]{},;:.?@" -> {
                val type = when (current()) {
                    '(' -> TokenType.LParen
                    ')' -> TokenType.RParen
                    '[' -> TokenType.LBracket
                    ']' -> TokenType.RBracket
                    '{' -> TokenType.LBrace
                    '}' -> TokenType.RBrace
                    ',' -> TokenType.Comma
                    ';' -> TokenType.Semicolon
                    ':' -> TokenType.Colon
                    '.' -> TokenType.Dot
                    '?' -> TokenType.Question
                    '@' -> TokenType.At
                    else -> TokenType.Unknown
                }
                if (type == TokenType.Unknown)
                    addDiagnostic("Unknown character '${current()}' at " +
                            "${cs.currentRow()} : ${cs.currentCol()}")
                emit(type, next().toString())
            }

            in Const.operators -> {
                val op = next()
                if (!eof() && current() in Const.doubleOperators) {
                    val dop = "$op${next()}"
                    emit(TokenType.Operator, dop)
                    return
                }
                emit(TokenType.Operator, op.toString())
            }
        }
    }

    fun parse() {
        while (!eof()) {
            while (skipComment() || skipWhiteSpace()) { /* do nothing */ }

            val start = cs.peek()
            when (start) {
                // Identifier & Keyword 标识符 & 关键字
                in Const.chars -> parseIdentifierAndKeyword()
                // Numbers (Integer, Decimal) 数字
                in Const.numbers -> parseNumbers()
                // String 字符串
                '\"' -> parseString()
                // Simple Tokens 简单符号
                else -> parseSimpleTokens()
            }
        }
        emit(TokenType.EOF)
        stream = TokenStream(tokens)
    }
}
