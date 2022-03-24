package com.github.landgrafhomyak.itmo_bevm.cli

@Suppress("SpellCheckingInspection")
expect fun eprintln(s: String)

expect fun exit(code: Int): Nothing