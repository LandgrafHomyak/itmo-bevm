package com.github.landgrafhomyak.itmo_bevm.cli

import kotlin.system.exitProcess

@Suppress("SpellCheckingInspection")
actual fun eprintln(s: String) {
    System.err.println(s)
}

actual fun exit(code: Int): Nothing {
    exitProcess(code)
}