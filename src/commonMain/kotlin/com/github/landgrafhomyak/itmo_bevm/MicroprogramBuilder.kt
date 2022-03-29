package com.github.landgrafhomyak.itmo_bevm

import kotlin.jvm.JvmStatic
import kotlin.reflect.KClass
import kotlin.reflect.safeCast


@Suppress("ClassName", "PropertyName", "SpellCheckingInspection", "unused", "FunctionName")
class MicroprogramBuilder {
    companion object {
        @JvmStatic
        fun microprogram(builder: MicroprogramBuilder.() -> Unit): MutableMicroprogram {
            val mp = MicroprogramBuilder().apply(builder)
            val commands = mp.objects.map { obj -> obj(mp.labels) }.toTypedArray().let { arr ->
                return@let arr + Array(256 - arr.size) { Microcommand.Empty }
            }

            return object : MutableMicroprogram {
                override val labels: MutableMap<String, UByte> = mp.labels
                override val commands: Array<Microcommand> = commands
            }
        }
    }

    private var mp: UByte = 0u
    private val labels = mutableMapOf<String, UByte>()
    private val objects = mutableListOf<(Map<String, UByte>) -> Microcommand>()
    operator fun String.unaryMinus(): UByte {
        if (this@unaryMinus in this@MicroprogramBuilder.labels)
            throw LabelDuplicationException(this@unaryMinus)
        this@MicroprogramBuilder.labels[this@unaryMinus] = this@MicroprogramBuilder.mp
        return this@MicroprogramBuilder.mp
    }

    operator fun Nothing?.invoke(count: UByte) {
        @Suppress("NAME_SHADOWING")
        var count = count
        while (count-- > 0u) {
            this@MicroprogramBuilder.objects.add { Microcommand.Empty }
            this@MicroprogramBuilder.mp++
        }
    }

    fun at(address: UByte) {
        if (address < this.mp) {
            throw IllegalArgumentException("Невозможно перезаписать микрокоманду по адресу 0x${address.toHex(2u)}")
        }
        while (this.mp < address) {
            this.objects.add { Microcommand.Empty }
            this.mp++
        }
    }

    private class IteratorScanner<T : Any>(
        private val iterator: Iterator<T>,
        private val bitsOrder: String
    ) {
        private var value: T? = null
        private val used = mutableSetOf<KClass<*>>()
        inline fun <reified R : Any> get(): R? {
            if (this.value == null) {
                if (!this.iterator.hasNext()) return null
                this.value = this.iterator.next()
            }
            for (ucls in this.used) {
                if (ucls.safeCast(this.value!!) != null) {
                    throw WrongMCBitOrderException(this.bitsOrder)
                }
            }
            this.used.add(R::class)
            return (this.value as? R)?.also { this.value = null }
        }

        fun assertEmpty() {
            if (this.value != null || this.iterator.hasNext()) {
                throw WrongMCBitOrderException(this.bitsOrder)
            }
        }
    }

    fun OP(vararg bits: Any) {
        this.mp++
        this.objects.add {
            val i = IteratorScanner(bits.iterator(), "{RDAC, RDBR, RDPS, RDIR}, {RDDR, RDCR, RDIP, RDSP}, COML, COMR, {PLS1, SORA}, {({LTOL, HTOL}, {HTOH, HTOL, SEXT}), (SHLT, SHL0), (SHRT, SHRF)}, SETC, SETV, STNZ, WRAC, WRDR, WRBR, WRCR, WRPS, WRIP, WRSP, WRAR, IO, INTS, HALT")

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
            i.assertEmpty()
            return@add mcc
        }
    }

    fun CTRL(vararg bits: Any) {
        this.mp++
        this.objects.add { labels ->
            val i = IteratorScanner(bits.iterator(), "{RDAC, RDBR, RDPS, RDIR}, {RDDR, RDCR, RDIP, RDSP}, COML, COMR, {PLS1, SORA}, {LTOL, HTOL}, {HTOH, HTOL}, 0b01010101, true, \"GOTO LABEL\"")
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
                    return@let labels[i.get<String>()!!]!!
                },
                cmpB
            )
            i.assertEmpty()
            return@add mcc
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