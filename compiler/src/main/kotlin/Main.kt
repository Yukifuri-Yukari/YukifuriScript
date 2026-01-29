package yukifuri.script.compiler

import yukifuri.script.compiler.interaction.cli.Cli
import yukifuri.script.compiler.test.TestController
import java.io.File

fun main(args: Array<String>) {
    val ctrl = TestController()

    val f = File("test/Recursive.yuki")
    ctrl.setup(f.readLines(), f.name)
    ctrl.mode = "BCG"
    ctrl.test()

    // runCli(args)
}

fun runCli(args: Array<String>) {
    val cli = Cli()
    if (args.isNotEmpty()) {
        val ctrl = cli.testCtrl
        for (f in args.map { File(it) }) {
            ctrl.setup(f.readLines(), f.name)
            ctrl.test()
        }
        return
    }
    cli.run()
}
