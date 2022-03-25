package com.github.landgrafhomyak.itmo_bevm.cli

sealed interface FileLike {
    fun close()

    interface Binary : FileLike {
        fun readAll(): UByteArray
        fun write(ba: UByteArray)
    }

    interface Text : FileLike {
        fun readAll(): String
        fun write(s: String)
    }
}