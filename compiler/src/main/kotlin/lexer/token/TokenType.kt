package yukifuri.script.compiler.lexer.token

enum class TokenType {
    Identifier,
    Keyword,
    IntegerLiteral,
    StringLiteral,
    NumberLiteral,
    // Operators are +-*/%<>=!&|, += -= etc, && || == != < > <= >= >> <<.
    Operator,

    // 符号 Symbols
    LParen,    // (
    RParen,    // )
    LBracket,  // [
    RBracket,  // ]
    LBrace,    // {
    RBrace,    // }
    Comma,     // ,
    Semicolon, // ;
    Colon,     // :
    Dot,       // .
    Question,  // ?
    At,        // @

    EOF,
    EOL,
    Unknown,
}
