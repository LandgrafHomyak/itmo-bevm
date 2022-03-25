package com.github.landgrafhomyak.itmo_bevm.cli

@Suppress("SpellCheckingInspection")
expect fun eprintln(s: String)

expect fun exit(code: Int): Nothing

expect class BinaryFile constructor(path: String) : FileLike.Binary

expect class TextFile constructor(path: String) : FileLike.Text

expect object BinaryStd: FileLike.Binary