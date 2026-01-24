package yukifuri.script.compiler.interaction.cli

import yukifuri.script.compiler.env.Environment
import yukifuri.script.compiler.exception.Diagnostics
import yukifuri.script.compiler.lexer.util.CharStreamImpl
import yukifuri.script.compiler.test.TestController
import yukifuri.utils.colorama.Fore
import java.io.File

class Cli {
    var coloring = true
    var debug = false
    var prompt = " $ "
    var enableESL = true

    val testCtrl = TestController()
    val ctx = CliContext()
    val commands: Map<String, Command> = mapOf(
        Command.new("help", "Print help messages.", listOf(0, 1)) {
            if (ctx.args.isNotEmpty()) {
                val cmd = commands[ctx.args[0]]
                if (cmd == null) {
                    color("Unknown command: ${ctx.args[0]}", Fore.LIGHT_RED_EX)
                    return@new
                }
                println("${cmd.name} - ${cmd.desc}")
                return@new
            }
            println("======== Commands ========")
            commands.forEach {
                println("${it.key} - ${it.value.desc}")
            }
        },
        Command.new("attr", "Edit attributes of cli.", listOf(-1)) {
            when (ctx.args.getOrNull(0)) {
                "set" -> when (ctx.args.getOrNull(1)) {
                    null -> color("attr set requires a name.")
                    "coloring" -> when (ctx.args.getOrNull(2)) {
                        "true", "false" -> coloring = ctx.args[2].toBoolean()
                            .also { testCtrl.coloring = it }
                        else -> color("Usage: attr set coloring <true|false>")
                    }

                    "esl" -> when (ctx.args.getOrNull(2)) {
                        "true", "false" -> enableESL = ctx.args[2].toBoolean()
                        else -> color("Usage: attr set esl <true|false>")
                    }

                    "prompt" -> prompt = when (ctx.args.getOrNull(2)) {
                        null -> " $ "
                        else -> ctx.args[2]
                    }
                }
            }
        },
        Command.new("load", "Load and execute the file or the files in directory.", listOf(1)) {
            val file = File(ctx.args[0])
            if (!file.exists()) {
                color("File not found: ${file.absolutePath}")
                return@new
            }
            val files = if (file.isDirectory) {
                file.listFiles()?.filter { it.isFile } ?: emptyList()
            } else {
                listOf(file)
            }

            for (file in files) {
                testCtrl.name = file.name
                testCtrl.text = file.readLines()
                testCtrl.diagnostics = Diagnostics(testCtrl.name, testCtrl.text)
                testCtrl.test()
                testCtrl.diagnostics.print()
            }
        },
    )

    fun run() {
        println("Yukifuri Script Cli (On ${Environment.os}, 26/01/24)")
        println("Enter \":help\" or \":help <command>\" for help")
        println("Type \":exit\" to exit.")
        println("If the shell cannot display ANSI Coloring, type \":attr set coloring false\"")
        println("Escape-Sequence-Lexing in strings is enabled by default, attr id: \"esl\".")

        var input: String
        while (true) {
            try {
                print(prompt)
                input = readln().trim()
                if (input == ":exit") break
                if (input.isEmpty()) continue
                if (input.startsWith(":")) {
                    val (cmd, args) = parseArgs(input)
                    debug("cmd=$cmd args=$args")
                    val command = commands[cmd]
                    if (command == null) {
                        color("Unknown command: $cmd", Fore.LIGHT_RED_EX)
                        continue
                    }

                    if (!command.arguments.map {
                            if (it == -1) true
                            else args.size == it
                        }.contains(true)) {
                        println("Usage: ${command.name} - ${command.desc}")
                        ctx.history.add(input)
                        continue
                    }

                    ctx.args = args
                    command.run(ctx)
                    ctx.history.add(input)
                    continue
                }
                testCtrl.name = "<stdin>"
                testCtrl.text = input.lines()
                testCtrl.diagnostics = Diagnostics(testCtrl.name, testCtrl.text)
                testCtrl.test()
            } catch (exception: Exception) {
                println("Error: ${exception.javaClass.simpleName}: ${exception.message ?: "<null>"}")
            }
        }
    }

    private fun color(str: String, color: String = Fore.LIGHT_RED_EX) {
        if (coloring)
            println("$color$str${Fore.RESET}")
        else println(str)
    }

    private fun parseArgs(input: String): Pair<String, List<String>> {
        val sb = StringBuilder()
        val list = mutableListOf<String>()
        val cs = CharStreamImpl(input)
        while (!cs.eof()) when (cs.peek()) {
            '"' -> {
                if (sb.isNotEmpty()) {
                    sb.append(cs.next())
                    continue
                }
                cs.next()
                if (enableESL) while (cs.peek() != '"') when (cs.peek()) {
                    '\\' -> {
                        if (cs.peek(2) == "\\\"") {
                            sb.append(cs.next())
                        }
                        sb.append(cs.next())
                    }
                    else -> sb.append(cs.next())
                } else while (cs.peek() != '"') sb.append(cs.next())
                cs.next()
                list.add(sb.toString())
                sb.clear()
            }
            ' ' -> {
                cs.next()
                if (sb.isNotEmpty()) {
                    list.add(sb.toString())
                    sb.clear()
                }
            }
            else -> sb.append(cs.next())
        }
        if (sb.isNotEmpty())
            list.add(sb.toString())
        return list[0].substring(1) to list.subList(1, list.size)
    }

    private fun debug(info: String) {
        if (debug)
            println("[DEBUG] $info")
    }
}
