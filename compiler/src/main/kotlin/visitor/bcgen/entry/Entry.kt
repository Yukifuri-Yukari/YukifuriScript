package yukifuri.script.compiler.visitor.bcgen.entry

import yukifuri.script.compiler.ast.base.Module
import yukifuri.script.compiler.ast.expr.*
import yukifuri.script.compiler.ast.flow.ConditionalFor
import yukifuri.script.compiler.ast.flow.ConditionalJump
import yukifuri.script.compiler.ast.function.FunctionCall
import yukifuri.script.compiler.ast.function.Return
import yukifuri.script.compiler.ast.function.YFunction
import yukifuri.script.compiler.ast.literal.Literal
import yukifuri.script.compiler.ast.structure.YFile
import yukifuri.script.compiler.ast.visitor.Visitor
import yukifuri.script.compiler.visitor.bcgen.entry.symbol.FunctionSym
import yukifuri.script.compiler.visitor.bcgen.entry.symbol.SymbolTable
import yukifuri.script.compiler.visitor.bcgen.entry.symbol.SymbolType
import yukifuri.script.compiler.visitor.bcgen.entry.symbol.VariableSym

class Entry : Visitor {
    val table = SymbolTable()

    private val typeResolver = TypeResolver(table)

    fun exec(file: YFile) {
        registerBuiltins()
        for (entry in file.table.functions()) {
            table.add(FunctionSym(entry.key, entry.value.returnType, entry.value))
        }
        file.module.accept(this)
    }

    fun registerBuiltins() {
        val emptyFunction = YFunction("", listOf(), "Nothing", Module.empty)
        table.add(FunctionSym("print", "Nothing", emptyFunction))
        table.add(FunctionSym("println", "Nothing", emptyFunction))
    }

    override fun functionDecl(decl: YFunction) {
        enter()
        for (entry in decl.args) {
            table.add(VariableSym(entry.first, false, entry.second))
        }
        val stmts = decl.body.list()
        for (stmt in stmts) {
            stmt.accept(this)
        }
        exit()
    }

    override fun functionCall(call: FunctionCall) {
        val func =
            table.root.find(call.name) ?:
            throw IllegalArgumentException("Function ${call.name} not found")
        func.assert(SymbolType.Function)
        func.used = true
    }

    override fun functionReturn(ret: Return) { }
    override fun literal(literal: Literal<*>) { }
    override fun binaryExpr(expr: BinaryExpr) { }
    override fun unaryExpr(expr: UnaryExpr) { }

    override fun getVariable(get: VariableGet) {
        val varSym =
            table.root.find(get.name) ?:
            throw IllegalArgumentException("Variable ${get.name} not found")
        varSym.assert(SymbolType.Variable)
        varSym.used = true
    }

    override fun declareVariable(decl: VariableDecl) {
        val type = typeResolver.getType(decl.value)
        if (decl.type != "auto" && type != decl.type)
            throw IllegalArgumentException("Type mismatch: ${decl.name} is ${decl.type}, but $type was expected")
        table.add(VariableSym(decl.name, decl.mutable, type))
    }

    override fun assignVariable(assign: VariableAssign) {
        val varSym =
            table.root.find(assign.name) ?:
            throw IllegalArgumentException("Variable ${assign.name} not found")
        varSym.assert(SymbolType.Variable)
        varSym as VariableSym
        if (!varSym.mutable)
            throw IllegalArgumentException("Variable ${assign.name} is immutable")
        val type = typeResolver.getType(assign.value)
        if (varSym.type != "auto" && type != varSym.type)
            throw IllegalArgumentException("Type mismatch: ${assign.name} is ${varSym.type}, but $type was expected")
        varSym.used = false // The variable may not be used after last assignment
    }

    override fun condFor(loop: ConditionalFor) {
        enter()
        loop.init.accept(this)
        loop.cond.accept(this)
        loop.updater.accept(this)
        loop.body.accept(this)
        exit()
    }

    override fun condJump(jump: ConditionalJump) {
        enter()
        jump.cond.accept(this)
        jump.ifBlock.accept(this)
        jump.elseBlock?.accept(this)
        exit()
    }

    var root: SymbolTable.SymbolScope = table.root

    fun enter() {
        table.enter()
        root = table.root
    }

    fun exit() {
        table.exit()
    }
}
