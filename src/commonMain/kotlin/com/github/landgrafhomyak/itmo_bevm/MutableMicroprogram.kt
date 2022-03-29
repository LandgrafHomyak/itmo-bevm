package com.github.landgrafhomyak.itmo_bevm


interface MutableMicroprogram:Microprogram {
    override val labels: MutableMap<String, UByte>
    override val commands: Array<Microcommand>
}