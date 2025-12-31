package yukifuri.script.compiler.ast.base

enum class Operator(
    val priority: Int,
    val op: String,
) {
    Add(0, "+"),
    Sub(0, "-"),
    Mul(1, "*"),
    Div(1, "/"),
    Mod(1, "%"),
    Lt(2, "<"),
    Lte(2, "<="),
    Gt(2, ">"),
    Gte(2, ">="),
    Eq(2, "=="),
    Neq(2, "!="),
    And(3, "&&"),
    Or(3, "||"),
    Lsh(4, "<<"),
    Rsh(4, ">>"),
    LogicalAnd(5, "&&"),
    LogicalOr(5, "||"),
    Not(6, "!"),
}
