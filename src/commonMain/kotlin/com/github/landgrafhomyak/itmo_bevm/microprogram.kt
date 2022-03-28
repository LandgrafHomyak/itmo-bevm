package com.github.landgrafhomyak.itmo_bevm

import kotlin.jvm.JvmStatic
import kotlin.reflect.KClass

@Suppress("ClassName", "PropertyName", "SpellCheckingInspection", "unused", "FunctionName")
sealed class microprogram {
    companion object {
        @JvmStatic
        operator fun invoke(builder: microprogram.() -> Unit): MicroprogramBundle {
            val labels = CalcLabels().apply(builder).labels
            val commands = Compile(labels).apply(builder).commands.toTypedArray()
            return MicroprogramBundle(labels, commands)
        }
    }

    abstract operator fun String.unaryMinus(): UByte
    abstract operator fun Nothing?.invoke(count: UByte)
    abstract fun OP(vararg bits: Any)
    abstract fun CTRL(vararg bits: Any)

    private class CalcLabels : microprogram() {
        private var mp: UByte = 0u
        val labels = mutableMapOf<String, UByte>()
        override fun String.unaryMinus(): UByte {
            if (this@unaryMinus in this@CalcLabels.labels)
                throw LabelDuplicationException(this@unaryMinus)
            this@CalcLabels.labels[this@unaryMinus] = this@CalcLabels.mp
            return this@CalcLabels.mp
        }

        override fun Nothing?.invoke(count: UByte) {
            this@CalcLabels.mp = (this@CalcLabels.mp + count).toUByte()
        }

        override fun OP(vararg bits: Any) {
            this.mp++
        }

        override fun CTRL(vararg bits: Any) {
            this.mp++
        }
    }

    private class Compile(val labels: Map<String, UByte>) : microprogram() {
        val commands = mutableListOf<Microcommand>()

        override fun String.unaryMinus(): UByte {
            return this@Compile.labels[this@unaryMinus]!!
        }

        override fun Nothing?.invoke(count: UByte) {
            @Suppress("NAME_SHADOWING")
            var count = count
            while (count-- > 0u) {
                this@Compile.commands.add(Microcommand.Empty)
            }
        }

        private class PostIterator<T : Any>(private val iterator: Iterator<T>) {
            private var value: T? = null
            private val used = mutableSetOf<KClass<*>>()
            inline fun <reified R : Any> get(): R? {
                if (this.value == null) {
                    if (!this.iterator.hasNext()) return null
                    this.value = this.iterator.next()
                }
                if (this.value!!::class in this.used) {
                    throw WrongMCBitOrderException()
                }
                this.used.add(R::class)
                return (this.value as? R)?.also { this.value = null }
            }

            fun hasNext() = this.iterator.hasNext()
        }

        override fun OP(vararg bits: Any) {
            val i = PostIterator(bits.iterator())

            @Suppress("RemoveExplicitTypeArguments")
            val mcc = Microcommand.Operation(
                i.get<RegisterReadingLeft>(),
                i.get<RegisterReadingRight>(),
                i.get<_COML>() != null,
                i.get<_COMR>() != null,
                i.get<ArithmeticalLogicUnitOperation>() ?: ArithmeticalLogicUnitOperation.PLS,
                i.get<CommutatorLoByteSource>()?.let { lo ->
                    ComutatorSourceCombination.Forward(lo, i.get<CommutatorHiByteSource>())
                } ?: i.get<CommutatorHiByteSource>()?.let { hi ->
                    ComutatorSourceCombination.Forward(null, hi)
                } ?: i.get<_SHLT>()?.let { _ ->
                    ComutatorSourceCombination.SHL(i.get<_SHL0>() != null)
                } ?: i.get<_SHRT>()?.let { _ ->
                    ComutatorSourceCombination.SHR(i.get<_SHRF>() != null)
                },
                i.get<_SETC>() != null,
                i.get<_SETV>() != null,
                i.get<_STNZ>() != null,
                i.get<_WRAC>() != null,
                i.get<_WRDR>() != null,
                i.get<_WRBR>() != null,
                i.get<_WRCR>() != null,
                i.get<_WRPS>() != null,
                i.get<_WRIP>() != null,
                i.get<_WRSP>() != null,
                i.get<_WRAR>() != null,
                i.get<MemoryAccess>(),
                i.get<_IO>() != null,
                i.get<_INTS>() != null,
                i.get<_HALT>() != null,
            )
            this.commands.add(mcc)
            if (i.hasNext()) throw WrongMCBitOrderException()
        }

        override fun CTRL(vararg bits: Any) {
            val i = PostIterator(bits.iterator())
            val chB: Array<Boolean>
            val cmpB: Boolean

            @Suppress("RemoveExplicitTypeArguments")
            val mcc = Microcommand.Control(
                i.get<RegisterReadingLeft>(),
                i.get<RegisterReadingRight>(),
                i.get<_COML>() != null,
                i.get<_COMR>() != null,
                i.get<ArithmeticalLogicUnitOperation>() ?: ArithmeticalLogicUnitOperation.PLS,
                i.get<CommutatorLoByteSource>(),
                i.get<CommutatorHiByteSource>(),
                (i.get<UByte>() ?: i.get<UShort>()?.toUByte() ?: i.get<UInt>()?.toUByte() ?: i.get<ULong>()?.toUByte())!!.let { v ->
                    chB = unpack(v.toULong(), 8u)
                    return@let chB[0]
                },
                chB[1],
                chB[2],
                chB[3],
                chB[4],
                chB[5],
                chB[6],
                chB[7],
                i.get<Boolean>()!!.let { b ->
                    cmpB = b
                    return@let this.labels[i.get<String>()!!]!!
                },
                cmpB
            )
            this.commands.add(mcc)
            if (i.hasNext()) throw WrongMCBitOrderException()
        }

    }

    val RDDR get() = RegisterReadingRight.RDDR
    val RDCR get() = RegisterReadingRight.RDCR
    val RDIP get() = RegisterReadingRight.RDIP
    val RDSP get() = RegisterReadingRight.RDSP
    val RDAC get() = RegisterReadingLeft.RDAC
    val RDBR get() = RegisterReadingLeft.RDBR
    val RDPS get() = RegisterReadingLeft.RDPS
    val RDIR get() = RegisterReadingLeft.RDIR

    private object _COMR

    val COMR get() = _COMR as Any

    private object _COML

    val COML get() = _COML as Any

    val PLS1 get() = ArithmeticalLogicUnitOperation.PLS1
    val SORA get() = ArithmeticalLogicUnitOperation.SORA
    val LTOL get() = CommutatorLoByteSource.LTOL
    val HTOL get() = CommutatorLoByteSource.HTOL
    val LTOH get() = CommutatorHiByteSource.LTOH
    val HTOH get() = CommutatorHiByteSource.HTOH
    val SEXT get() = CommutatorHiByteSource.SEXT

    private object _SHLT

    val SHLT get() = _SHLT as Any

    private object _SHL0

    val SHL0 get() = _SHL0 as Any

    private object _SHRT

    val SHRT get() = _SHRT as Any

    private object _SHRF

    val SHRF get() = _SHRF as Any

    private object _SETC

    val SETC get() = _SETC as Any

    private object _SETV

    val SETV get() = _SETV as Any

    private object _STNZ

    val STNZ get() = _STNZ as Any

    sealed interface _WR
    private object _WRDR : _WR

    val WRDR get() = _WRDR as _WR

    private object _WRCR : _WR

    val WRCR get() = _WRCR as _WR

    private object _WRIP : _WR

    val WRIP get() = _WRIP as _WR

    private object _WRSP : _WR

    val WRSP get() = _WRSP as _WR

    private object _WRAC : _WR

    val WRAC get() = _WRAC as _WR

    private object _WRBR : _WR

    val WRBR get() = _WRBR as _WR

    private object _WRPS : _WR

    val WRPS get() = _WRPS as _WR

    private object _WRAR : _WR

    val WRAR get() = _WRAR as _WR

    val LOAD get() = MemoryAccess.LOAD
    val STOR get() = MemoryAccess.STOR

    private object _IO

    val IO get() = _IO as Any

    private object _INTS

    val INTS get() = _INTS as Any

    private object _HALT

    val HALT get() = _HALT as Any

    inline fun mov(from: RegisterReading, vararg to: _WR) {
        this.OP(from, LTOL, HTOH, *to)
    }

    inline fun sum(fromL: RegisterReadingLeft, fromR: RegisterReadingRight, vararg to: _WR) {
        this.OP(fromL, fromR, LTOL, HTOH, *to)
    }
}