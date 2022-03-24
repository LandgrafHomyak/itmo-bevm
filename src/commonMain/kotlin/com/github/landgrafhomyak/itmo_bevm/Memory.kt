package com.github.landgrafhomyak.itmo_bevm

@Suppress("MemberVisibilityCanBePrivate", "CanBeParameter")
class Memory internal constructor(val size: UInt) {
    private val data = Array(this.size.toInt()) { Byte.uninitialized() }

    operator fun get(address: UInt) = this.data[address.toInt()]
}