package com.github.landgrafhomyak.itmo_bevm

import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

/**
 * Минимальная адресуемая единица памяти в БЭВМ
 *
 * Так как регистры имеют размер в 1 байт, так же используются и для их представления
 *
 * Биты хранятся в [little endian](https://ru.wikipedia.org/wiki/%D0%9F%D0%BE%D1%80%D1%8F%D0%B4%D0%BE%D0%BA_%D0%B1%D0%B0%D0%B9%D1%82%D0%BE%D0%B2#%D0%9F%D0%BE%D1%80%D1%8F%D0%B4%D0%BE%D0%BA_%D0%BE%D1%82_%D0%BC%D0%BB%D0%B0%D0%B4%D1%88%D0%B5%D0%B3%D0%BE_%D0%BA_%D1%81%D1%82%D0%B0%D1%80%D1%88%D0%B5%D0%BC%D1%83):
 * * 0-й == 2 ** 0
 * * 1-й == 2 ** 1
 * * 2-й == 2 ** 2
 * * и т.д.
 */
@Suppress("MemberVisibilityCanBePrivate", "unused", "SpellCheckingInspection")
class BevmByte private constructor(private val bits: Array<Boolean>) {
    companion object {
        /**
         * Размер в битах
         */
        const val BITS_SIZE = 16u

        /**
         * Создаёт объект без инициализации
         */
        @Suppress("RemoveRedundantQualifierName")
        fun uninitialized() = BevmByte(Array(BevmByte.BITS_SIZE.toInt()) { false })

        fun fromUnsigned(u: UInt) = BevmByte(Array(this.BITS_SIZE.toInt()) { i -> (u and (1u shl i) != 0u) })

        fun fromBits(vararg bits: Boolean) = BevmByte(arrayOf(*bits.toTypedArray()))
        fun fromReversedBits(vararg bits: Boolean) = BevmByte(bits.reversed().toTypedArray())
    }

    init {
        @Suppress("RemoveRedundantQualifierName")
        if (this.bits.size.toUInt() != BevmByte.BITS_SIZE) {
            throw IllegalArgumentException()
        }
    }

    /**
     * Интерпретирует биты как беззнаковое целое число и возвращает конвертированное значение
     */
    fun toUnsigned(): UInt {
        var value: UInt = 0u
        for (bit in this.bits.reversed()) {
            value = (value shl 1) or (if (bit) 1u else 0u)
        }
        return value
    }

    /**
     * Интерпретирует биты как знаковое целое число и возвращает конвертированное значение
     */
    @Suppress("RemoveRedundantQualifierName")
    fun toSigned(): Int = (this.toUnsigned() - (1u shl BevmByte.BITS_SIZE.toInt())).toInt()

    /**
     * Сокращение от value & (1 << bit)
     */
    operator fun get(bit: UInt): Boolean = this.bits[bit.toInt()]

    /**
     * Сокращение от value | (1 << bit) и value & ~(1 << bit)
     */
    operator fun set(bit: UInt, value: Boolean) {
        this.bits[bit.toInt()] = value
    }

    /**
     * See [bitProperty]
     */
    inner class BitProperty internal constructor(private val bit: UInt) : ReadWriteProperty<Any?, Boolean> {
        override fun getValue(thisRef: Any?, property: KProperty<*>): Boolean = this@BevmByte[this@BitProperty.bit]

        override fun setValue(thisRef: Any?, property: KProperty<*>, value: Boolean) {
            this@BevmByte[this@BitProperty.bit] = value
        }
    }

    /**
     * Возвращает делегируемый объект для доступа к биту через отдельную переменную
     */
    fun bitProperty(bit: UInt) = this.BitProperty(bit)

    @Suppress("RemoveRedundantQualifierName")
    var signBit by this.bitProperty(BevmByte.BITS_SIZE - 1u)

    @Suppress("RemoveRedundantQualifierName")
    operator fun not() = BevmByte(Array(BevmByte.BITS_SIZE.toInt()) { i -> !this[i.toUInt()] })

    @Suppress("RemoveRedundantQualifierName")
    infix fun shl(offset: UInt): BevmByte = BevmByte(Array(BevmByte.BITS_SIZE.toInt()) { i -> if (i.toUInt() < offset) false else this[i.toUInt() - offset] })

    @Suppress("RemoveRedundantQualifierName")
    infix fun shr(offset: UInt): BevmByte = BevmByte(Array(BevmByte.BITS_SIZE.toInt()) { i -> if (i.toUInt() >= BevmByte.BITS_SIZE - offset) this.signBit else this[i.toUInt() + offset] })

    @Suppress("RemoveRedundantQualifierName")
    infix fun ushr(offset: UInt): BevmByte = BevmByte(Array(BevmByte.BITS_SIZE.toInt()) { i -> if (i.toUInt() >= BevmByte.BITS_SIZE - offset) false else this[i.toUInt() + offset] })

    fun isZero(): Boolean = !this.bits.any()

    fun bit(bit: UInt) = this[bit]

    operator fun plus(other: UInt) = this.plus(other, null)
    operator fun plus(other: BevmByte) = this.plus(other, null)

    @Suppress("RemoveRedundantQualifierName")
    fun plus(other: UInt, flags: Processor<*>.Flags?): BevmByte = this.plus(BevmByte.fromUnsigned(other), flags)
    fun plus(other: BevmByte, flags: Processor<*>.Flags?): BevmByte {
        var carry = false
        var overflow = false
        @Suppress("RemoveRedundantQualifierName")
        return BevmByte.fromBits(*(0u until BevmByte.BITS_SIZE).map { b ->
            return@map (this[b] xor other[b] xor carry).also {
                carry = ((this[b] and other[b]) or (this[b] and carry) or (other[b] and carry)).also { newCarry -> overflow = newCarry xor carry }
            }
        }.toBooleanArray()).also { result ->
            flags?.also {
                flags.recalcFrom(result)
                flags.carry = carry
                flags.overflow = overflow
            }
        }
    }

//    fun copy() = BevmByte(this.bits)

    operator fun unaryMinus() = !this + 1u

    @Suppress("RemoveRedundantQualifierName")
    operator fun minus(other: UInt) = this + -BevmByte.fromUnsigned(other)

    operator fun inc() = this + 1u
    operator fun dec() = this - 1u

    operator fun get(range: UIntRange): BevmByte {
        @Suppress("RemoveRedundantQualifierName")
        val filtered = BevmByte.fromUnsigned(0u)
        for (b in range) {
            filtered[b] = this[b]
        }
        return filtered
    }

    infix fun and(other: BevmByte): BevmByte {
        @Suppress("RemoveRedundantQualifierName")
        return BevmByte.fromBits(*(0u until BevmByte.BITS_SIZE).map { b ->
            return@map this[b] and other[b]
        }.toBooleanArray())
    }

    infix fun or(other: BevmByte): BevmByte {
        @Suppress("RemoveRedundantQualifierName")
        return BevmByte.fromBits(*(0u until BevmByte.BITS_SIZE).map { b ->
            return@map this[b] or other[b]
        }.toBooleanArray())
    }

    fun formatToString() = this.toUnsigned().toString(16).padStart(4, '0')
    fun formatToStringP() = this.toUnsigned().toString(16).padStart(3, '0')
}