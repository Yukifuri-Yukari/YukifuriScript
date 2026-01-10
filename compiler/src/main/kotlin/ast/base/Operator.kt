package yukifuri.script.compiler.ast.base

enum class Operator(
    val priority: Int,
    val op: String,
) {
    // Assign
    Assign(0, "="),
    AddAssign(0, "+="),
    SubAssign(0, "-="),
    MulAssign(0, "*="),
    DivAssign(0, "/="),
    ModAssign(0, "%="),

    // Logical
    Or(1, "||"),
    LogicalOr(1, "||"),
    And(2, "&&"),
    LogicalAnd(2, "&&"),

    // Compare Equals
    Eq(3, "=="),
    Neq(3, "!="),

    // Compare
    Lt(4, "<"),
    Lte(4, "<="),
    Gt(4, ">"),
    Gte(4, ">="),

    // Shift
    Lsh(5, "<<"),
    Rsh(5, ">>"),

    // Maths
    Add(6, "+"),
    Sub(6, "-"),
    Mul(7, "*"),
    Div(7, "/"),
    Mod(7, "%"),

    // Unary
    Inc(8, "++"),
    Dec(8, "--"),
    Not(9, "!"),

    ;
    companion object {
        val unary = setOf(Inc, Dec, Not, Sub, Add).map { it.op }.toSet()
    }
}
