package com.github.landgrafhomyak.itmo_bevm.cli

interface FileLike {
    fun readAll()
    fun write()
    fun close()
}