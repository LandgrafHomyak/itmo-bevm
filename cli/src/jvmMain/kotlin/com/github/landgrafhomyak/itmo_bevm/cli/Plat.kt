@file:Suppress("EXPERIMENTAL_IS_NOT_ENABLED")
@file:OptIn(ExperimentalUnsignedTypes::class)

package com.github.landgrafhomyak.itmo_bevm.cli

import java.io.File
import kotlin.system.exitProcess

@Suppress("SpellCheckingInspection")
actual fun eprintln(s: String) {
    System.err.println(s)
}

actual fun exit(code: Int): Nothing {
    exitProcess(code)
}

actual class BinaryFile actual constructor(path: String) : FileLike.Binary {
    private val real = File(path)

    override fun readAll(): UByteArray = this.real.readBytes().toUByteArray()

    override fun write(ba: UByteArray) {
        this.real.writeBytes(ba.toByteArray())
    }

    override fun create() {
        this.real.delete()
        this.real.createNewFile()
    }
}

actual class TextFile actual constructor(path: String) : FileLike.Text {
    private val real = File(path)

    override fun readAll(): String = this.real.readText(Charsets.UTF_8)

    override fun write(s: String) {
        this.real.writeText(s, Charsets.UTF_8)
    }

    override fun create() {
        this.real.delete()
        this.real.createNewFile()
    }
}

actual object BinaryStd : FileLike.Binary {

    override fun readAll(): UByteArray = System.`in`.readAllBytes().toUByteArray()

    override fun write(ba: UByteArray) {
        System.out.write(ba.toByteArray())
    }

    override fun create() {}
}

actual object TextStd : FileLike.Text {
    override fun readAll(): String = System.`in`.readAllBytes().toString(Charsets.UTF_8)

    override fun write(s: String) {
        print(s)
    }

    override fun create() {}
}