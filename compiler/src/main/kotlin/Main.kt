package yukifuri.script.compiler

import yukifuri.script.compiler.exception.Diagnostics
import yukifuri.script.compiler.interaction.cli.Cli
import yukifuri.script.compiler.visitor.bcgen.vm.Bytecodes
import java.io.File

fun main(args: Array<String>) {
    Bytecodes.generate()
    runCli(args)
}

fun runCli(args: Array<String>) {
    val cli = Cli()
    if (args.isNotEmpty()) {
        val ctrl = cli.testCtrl
        for (f in args.map { File(it) }) {
            val text = f.readLines()
            ctrl.name = f.name
            ctrl.text = text
            ctrl.diagnostics = Diagnostics(f.name, text)
            ctrl.coloring = false
            ctrl.test()
        }
        return
    }
    cli.run()
}
