package com.github.landgrafhomyak.itmo_bevm.cli


fun mainEmbeddable(argv: Array<String>): Int {
    val argi = argv.iterator()
    var inFile: String? = null
    var outFile: String? = null
    var ip: String? = null

    if (!argi.hasNext()) {
        eprintln("Command not specified, see 'bevm help' for more info")
        return 1
    }

    val commandName = argi.next()
    val command = Commands.byAlias(commandName)
    if (command == null) {
        eprintln("Unknown command '$commandName', see 'bevm help' for more info")
        return 2
    }
    command.execute(mapOf())
    return 0
}

fun mainNative(argv: Array<String>) {
    exit(mainEmbeddable(argv))
}