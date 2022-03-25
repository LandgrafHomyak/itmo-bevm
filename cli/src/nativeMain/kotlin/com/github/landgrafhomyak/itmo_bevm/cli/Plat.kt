@file:Suppress("EXPERIMENTAL_IS_NOT_ENABLED")
@file:OptIn(ExperimentalUnsignedTypes::class)

package com.github.landgrafhomyak.itmo_bevm.cli

import kotlinx.cinterop.ByteVar
import kotlinx.cinterop.UByteVar
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.get
import kotlinx.cinterop.reinterpret
import kotlinx.cinterop.toKString
import kotlinx.cinterop.toKStringFromUtf8
import kotlinx.cinterop.usePinned
import platform.posix.SEEK_END
import platform.posix.fclose
import platform.posix.fopen
import platform.posix.fprintf
import platform.posix.fputs
import platform.posix.fread
import platform.posix.free
import platform.posix.fseek
import platform.posix.ftell
import platform.posix.fwrite
import platform.posix.malloc
import platform.posix.stderr
import platform.posix.stdin
import platform.posix.stdout
import kotlin.system.exitProcess

inline fun Int.toSizeT() = this.toULong()

@Suppress("SpellCheckingInspection")
actual fun eprintln(s: String) {
    fprintf(stderr, "%s\n", s)
}

actual fun exit(code: Int): Nothing {
    exitProcess(code)
}

actual class BinaryFile actual constructor(path: String) : FileLike.Binary {
    private val real = fopen(path, "ba+")

    override fun readAll(): UByteArray {
        val len = nativeGetLength(this.real)
        return ByteArray(len).usePinned { pinned ->
            nativeReadAll(pinned.addressOf(0), len, this@BinaryFile.real)
            return@usePinned pinned.get()
        }.toUByteArray()
    }

    override fun write(ba: UByteArray) {
        ba.usePinned { pinned ->
            fwrite(pinned.addressOf(0), 1, ba.size.toSizeT(), this.real)
        }
    }

    override fun close() {
        fclose(real)
    }
}

actual class TextFile actual constructor(path: String) : FileLike.Text {
    private val real = fopen(path, "ta+")

    override fun readAll(): String {
        val len = nativeGetLength(this.real)
        return ByteArray(len + 1).usePinned { pinned ->
            nativeReadAll(pinned.addressOf(0), len, this@TextFile.real)
            pinned.get()[len] = 0
            return@usePinned pinned.addressOf(0).toKStringFromUtf8()
        }
    }

    override fun write(s: String) {
        fputs(s, this.real)
    }

    override fun close() {
        fclose(real)
    }
}

actual object BinaryStd : FileLike.Binary {
    override fun readAll(): UByteArray {
        val len = nativeGetLength(stdin)
        return ByteArray(len).usePinned { pinned ->
            nativeReadAll(pinned.addressOf(0), len, stdin)
            return@usePinned pinned.get()
        }.toUByteArray()
    }

    override fun write(ba: UByteArray) {
        ba.usePinned { pinned ->
            fwrite(pinned.addressOf(0), 1, ba.size.toSizeT(), stdout)
        }
    }

    override fun close() {}
}