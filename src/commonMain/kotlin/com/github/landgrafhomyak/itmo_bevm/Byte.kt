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
@Suppress("MemberVisibilityCanBePrivate", "unused")
class Byte private constructor(private val bits: Array<Boolean>) {
    companion object {
        /**
         * Размер в битах
         */
        const val BITS_SIZE = 16u

        /**
         * Создаёт объект без инициализации
         */
        @Suppress("RemoveRedundantQualifierName")
        fun uninitialized() = Byte(Array(Byte.BITS_SIZE.toInt()) { false })

    }

    init {
        @Suppress("RemoveRedundantQualifierName")
        if (this.bits.size.toUInt() != Byte.BITS_SIZE) {
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
    fun toSigned(): Int = (this.toUnsigned() - (1u shl Byte.BITS_SIZE.toInt())).toInt()

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
        override fun getValue(thisRef: Any?, property: KProperty<*>): Boolean = this@Byte[this@BitProperty.bit]

        override fun setValue(thisRef: Any?, property: KProperty<*>, value: Boolean) {
            this@Byte[this@BitProperty.bit] = value
        }
    }

    /**
     * Возвращает делегируемый объект для доступа к биту через отдельную переменную
     */
    fun bitProperty(bit: UInt) = this.BitProperty(bit)
}