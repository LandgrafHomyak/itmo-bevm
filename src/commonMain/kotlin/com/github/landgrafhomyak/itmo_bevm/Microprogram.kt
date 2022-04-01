package com.github.landgrafhomyak.itmo_bevm


interface Microprogram {
    val labels: Map<String, UByte>
    val commands: Array<out Microcommand>

    fun copy(): MutableMicroprogram {
        return object : MutableMicroprogram() {
            override val labels: MutableMap<String, UByte> = mutableMapOf<String, UByte>().apply { putAll(this@Microprogram.labels) }
            override val commands: Array<Microcommand> = arrayOf(*this@Microprogram.commands)
            override fun get(address: UByte): Microcommand = this.commands[address.toInt()]
            override val entryPointAddress: UByte = this@Microprogram.entryPointAddress
        }
    }

    companion object {
        const val MICROPROGRAM_SIZE = 256u
    }

    operator fun get(address: UByte): Microcommand

    val entryPointAddress: UByte
}