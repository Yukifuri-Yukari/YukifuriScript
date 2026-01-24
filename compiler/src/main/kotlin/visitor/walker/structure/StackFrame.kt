package yukifuri.script.compiler.walker.structure

import yukifuri.script.compiler.util.Pair3
import yukifuri.script.compiler.walker.obj.NumberObject
import yukifuri.script.compiler.walker.obj.Object
import java.util.Stack

class StackFrame(
    val stack: Stack<Object> = Stack(),
    var localVars: MutableMap<
            String, Pair3<Object, String, Boolean>
            > = mutableMapOf()
) {

    fun push(value: Object) {
        stack.push(value)
    }

    fun pop(): Object = stack.pop()

    fun store(name: String, value: Object, type: String, mutable: Boolean) {
        localVars[name] = Pair3(value, type, mutable)
    }

    fun load(name: String): Pair3<Object, String, Boolean>? = localVars[name]

    operator fun get(name: String) = load(name)
    operator fun set(name: String, p3: Pair3<Object, String, Boolean>) {
        localVars[name] = p3
    }

    override fun toString(): String {
        return "StackFrame(stack=$stack, localVars=$localVars)"
    }
}
