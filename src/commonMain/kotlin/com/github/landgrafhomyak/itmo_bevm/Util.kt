@file:Suppress("unused")

package com.github.landgrafhomyak.itmo_bevm

private fun pack(bits: Iterable<Boolean>): ULong {
    var sum: ULong = 0uL
    for (bit in bits) {
        sum = (sum shl 1) or (if (bit) 1u else 0u)
    }
    return sum
}

fun packLE(vararg bits: Boolean) = pack(bits.reversed())

fun packBE(vararg bits: Boolean) = pack(bits.asIterable())

fun unpackLE(value: ULong, bitsCount: UByte): Array<Boolean> = Array(bitsCount.toInt()) { i ->
    (value and (1uL shl i)) != 0uL
}

fun unpackBE(value: ULong, bitsCount: UByte): Array<Boolean> = unpackLE(value, bitsCount).reversedArray()

operator fun <T> Array<T>.get(i: UInt) = this[i.toInt()]
operator fun <T> Array<T>.get(i: ULong) = this[i.toInt()]
operator fun <T> Array<T>.set(i: UInt, v: T) {
    this[i.toInt()] = v
}

operator fun <T> List<T>.get(i: UInt) = this[i.toInt()]
operator fun <T> List<T>.get(i: ULong) = this[i.toInt()]
operator fun <T> MutableList<T>.set(i: UInt, v: T) {
    this[i.toInt()] = v
}

operator fun <T> MutableList<T>.set(i: ULong, v: T) {
    this[i.toInt()] = v
}

fun UByte.toHex(tetrads: UByte) = this@toHex.toString(16).padStart(tetrads.toInt(), '0')

inline fun <reified T> Array<T>.padEnd(size: UInt, elem: T) = this@padEnd + Array(size.toInt() - this.size) { elem }

fun microprogramWithBuildInfo(builder: MicroprogramBuilder.() -> Unit) = MicroprogramBuilder.build(builder)
fun microprogram(builder: MicroprogramBuilder.() -> Unit) = microprogramWithBuildInfo(builder) as MutableMicroprogram

