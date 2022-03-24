package com.github.landgrafhomyak.itmo_bevm.cli

import platform.posix.fprintf
import platform.posix.stderr
import kotlin.system.exitProcess

@Suppress("SpellCheckingInspection")
actual fun eprintln(s: String) {
    fprintf(stderr, "%s\n", s)
}

actual fun exit(code: Int): Nothing {
    exitProcess(code)
}