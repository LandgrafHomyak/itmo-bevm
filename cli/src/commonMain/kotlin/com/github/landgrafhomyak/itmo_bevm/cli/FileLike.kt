@file:Suppress("EXPERIMENTAL_IS_NOT_ENABLED")
@file:OptIn(ExperimentalUnsignedTypes::class)

package com.github.landgrafhomyak.itmo_bevm.cli

sealed interface FileLike {
    fun create()

    interface Binary : FileLike {
        fun readAll():  Array<UByte>
        fun write(ba: Array<UByte>)
    }

    interface Text : FileLike {
        fun readAll(): String
        fun write(s: String)
    }
}