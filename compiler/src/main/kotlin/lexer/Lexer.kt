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
        if (eof()) return false
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
                        next(1)
                    }
                    if (peek(2) == "*/") {
                        domain--
                        next(1)
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
        emit(TokenType.StringLiteral, builder.toString())
    }

    private fun parseFloatings(start: String = "") {
        val builder = StringBuilder()
        builder.append(start)
        var hasE = false
        var hasDot = '.' in start
        while (!eof() && current() in Const.scientificNotation) {
            when (current().lowercaseChar()) {
                'e' -> {
                    if (hasE) {
                        addDiagnostic("Invalid floating point number.")
                        throwCE("Compile Error")
                    }
                    hasE = true
                    builder.append(next())
                    if (peek() == '_') {
                        addDiagnostic("Invalid usage of underscore in scientific notation.")
                        throwCE("Compile Error")
                    }
                    builder.append(next())
                    continue
                }
                '.' -> {
                    if (hasDot) {
                        addDiagnostic("Invalid floating point number.")
                        throwCE("Compile Error")
                    }
                    hasDot = true
                }
            }
            builder.append(next())
        }
        emit(TokenType.Decimal, builder.toString())
    }

    private fun collect(set: Set<Char>): String {
        val builder = StringBuilder()
        while (!eof() && current() in set) {
            builder.append(next())
        }
        return builder.toString()
    }

    private fun parseNumbers() {
        if (current() !in Const.validNumbers) return
        if (current() == '.') return parseFloatings()
        if (current() == '0') when (val possiblePrefix = next(2)[1]) {
            'x' -> return emit(
                TokenType.Integer,
                "0${possiblePrefix}${collect(Const.hexNumbers)}"
            )
            'o' -> return emit(
                TokenType.Integer,
                "0${possiblePrefix}${collect(Const.octNumbers)}"
            )
            'b' -> return emit(
                TokenType.Integer,
                "0${possiblePrefix}${collect(Const.binNumbers)}"
            )
            '.' -> return parseFloatings()
        }
        // Cases of int and float starts with '1' .. '9'
        val builder = StringBuilder()
        while (!eof()) {
            if (peek() in setOf('e', '.', 'E')) {
                return parseFloatings(builder.toString())
            }
            if (current() !in Const.validNumbers)
                break
            builder.append(next())
        }
        emit(TokenType.Integer, builder.toString())
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
                if (!eof() && (op.toString() + current()) in Const.mulOperators) {
                    val dop = "$op${next()}"
                    emit(TokenType.Operator, dop)
                    return
                }
                emit(TokenType.Operator, op.toString())
            }
        }
    }

    fun parse() {
        while (true) {
            while (skipWhiteSpace() || skipComment()) { /* do nothing */ }
            if (eof()) break

            val start = peek()
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
