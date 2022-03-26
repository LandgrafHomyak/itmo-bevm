@file:Suppress("EXPERIMENTAL_IS_NOT_ENABLED")
@file:OptIn(ExperimentalUnsignedTypes::class)

package com.github.landgrafhomyak.itmo_bevm.cli

fun viewBin(file: FileLike.Text, data: Array<UByte>, len: UInt, word: Boolean) {
    @Suppress("NAME_SHADOWING")
    val data = if (word) {
        (data.indices step 2).map { i -> (data[i].toUInt() shl 8) or (if (i + 1 < data.size) data[i + 1].toUInt() else 0u) }.toUIntArray()
    } else {
        data.map(UByte::toUInt).toUIntArray()
    }
    val addressSize = data.size.toString(16).length
    for (pos in data.indices step len.toInt()) {
        file.write("0x${pos.toString(16).padStart(addressSize, '0')} | ")
        file.write(data.slice(pos until min(pos + len.toInt(), data.size)).joinToString(separator = " ") { b -> b.toString(16).padStart(if (word) 4 else 2, '0') })
        file.write("\n")
    }
}