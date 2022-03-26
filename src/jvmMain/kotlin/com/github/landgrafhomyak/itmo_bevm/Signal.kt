package com.github.landgrafhomyak.itmo_bevm

actual open class Signal : Throwable() {
    override fun fillInStackTrace(): Throwable = this
}