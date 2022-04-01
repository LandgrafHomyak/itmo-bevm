package com.github.landgrafhomyak.itmo_bevm

@Suppress("SpellCheckingInspection")
interface AbstractBevmByte {
    val bit15: Boolean
    val bit14: Boolean
    val bit13: Boolean
    val bit12: Boolean
    val bit11: Boolean
    val bit10: Boolean
    val bit9: Boolean
    val bit8: Boolean
    val bit7: Boolean
    val bit6: Boolean
    val bit5: Boolean
    val bit4: Boolean
    val bit3: Boolean
    val bit2: Boolean
    val bit1: Boolean
    val bit0: Boolean

}

@Suppress("SpellCheckingInspection")
data class BevmByteSum(
    val sum: BevmByte, val carry: Boolean, val overflow: Boolean
)

fun AbstractBevmByte.plus(other: AbstractBevmByte, oldCarry: Boolean = false): BevmByteSum {
    var carry = oldCarry
    var overflow = false

    @Suppress("SpellCheckingInspection")
    val summator: (Boolean, Boolean) -> Boolean = summator@{ t, o ->
        return@summator (t xor o xor carry).also {
            carry = ((t and o) or (t and carry) or (o and carry)).also { newCarry -> overflow = newCarry xor carry }
        }
    }
    val sum = arrayOf(
        summator(this.bit0, other.bit0),
        summator(this.bit1, other.bit1),
        summator(this.bit2, other.bit2),
        summator(this.bit3, other.bit3),
        summator(this.bit4, other.bit4),
        summator(this.bit5, other.bit5),
        summator(this.bit6, other.bit6),
        summator(this.bit7, other.bit7),
        summator(this.bit8, other.bit8),
        summator(this.bit9, other.bit9),
        summator(this.bit10, other.bit10),
        summator(this.bit11, other.bit11),
        summator(this.bit12, other.bit12),
        summator(this.bit13, other.bit13),
        summator(this.bit14, other.bit14),
        summator(this.bit15, other.bit15),
    )

    return BevmByte(
        sum[15], sum[14], sum[13], sum[12], sum[11], sum[10], sum[9], sum[8], sum[7], sum[6], sum[5], sum[4], sum[3], sum[2], sum[1], sum[0]
    ).let { b -> BevmByteSum(b, carry, overflow) }
}

infix fun AbstractBevmByte.and(other: AbstractBevmByte) = BevmByte(
    this.bit15 and other.bit15,
    this.bit14 and other.bit14,
    this.bit13 and other.bit13,
    this.bit12 and other.bit12,
    this.bit11 and other.bit11,
    this.bit10 and other.bit10,
    this.bit9 and other.bit9,
    this.bit8 and other.bit8,
    this.bit7 and other.bit7,
    this.bit6 and other.bit6,
    this.bit5 and other.bit5,
    this.bit4 and other.bit4,
    this.bit3 and other.bit3,
    this.bit2 and other.bit2,
    this.bit1 and other.bit1,
    this.bit0 and other.bit0,
)

val AbstractBevmByte.signBit get() = this.bit15

operator fun AbstractBevmByte.not() = BevmByte(
    !this.bit15, !this.bit14, !this.bit13, !this.bit12,
    !this.bit11, !this.bit10, !this.bit9, !this.bit8,
    !this.bit7, !this.bit6, !this.bit5, !this.bit4,
    !this.bit3, !this.bit2, !this.bit1, !this.bit0,
)

@Suppress("SpellCheckingInspection")
fun AbstractBevmByte.toBevmAddress() = BevmAddress(this)

fun AbstractBevmByte.toUnsigned() = packBE(
    this.bit15, this.bit14, this.bit13, this.bit12,
    this.bit11, this.bit10, this.bit9, this.bit8,
    this.bit7, this.bit6, this.bit5, this.bit4,
    this.bit3, this.bit2, this.bit1, this.bit0,
)

fun AbstractBevmByte.isZero() = !(this.bit15 or this.bit14 or this.bit13 or this.bit12 or this.bit11 or this.bit10 or this.bit9 or this.bit8 or this.bit7 or this.bit6 or this.bit5 or this.bit4 or this.bit3 or this.bit2 or this.bit1 or this.bit0)