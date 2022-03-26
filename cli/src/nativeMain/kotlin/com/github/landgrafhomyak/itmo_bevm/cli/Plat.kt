@file:Suppress("EXPERIMENTAL_IS_NOT_ENABLED")
@file:OptIn(ExperimentalUnsignedTypes::class)

package com.github.landgrafhomyak.itmo_bevm.cli

import kotlinx.cinterop.CPointer
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.cstr
import kotlinx.cinterop.toKString
import kotlinx.cinterop.usePinned
import platform.posix.FILE
import platform.posix.fclose
import platform.posix.fopen
import platform.posix.fprintf
import platform.posix.freopen
import platform.posix.fwrite
import platform.posix.stderr
import platform.posix.stdin
import platform.posix.stdout
import platform.posix.strlen
import kotlin.system.exitProcess

inline fun Int.toSizeT() = this.toULong()

@Suppress("SpellCheckingInspection")
actual fun eprintln(s: String) {
    fprintf(stderr, "%s\n", s)
}

actual fun exit(code: Int): Nothing {
    exitProcess(code)
}

private fun commonReadAll(file: CPointer<FILE>): ByteArray {
    val chunks = mutableListOf<List<Byte>>()
    while (true) {
        chunks.add(ByteArray(256).usePinned { pinned ->
            val read = nativeRead(pinned.addressOf(0), 256, file)
            return@usePinned pinned.get().slice(0 until read)
        })
        if (chunks.last().size < 256) break
    }
    return chunks.flatten().toByteArray()
}

actual class BinaryFile actual constructor(private val path: String) : FileLike.Binary {

    override fun readAll(): Array<UByte> {
        val file = fopen(this.path, "rb") ?: throw FileAccessError(this.path)
        return commonReadAll(file).toUByteArray().also { fclose(file) }.toTypedArray()
    }

    override fun write(ba: Array<UByte>) {
        val file = fopen(this.path, "ab") ?: throw FileAccessError(this.path)
        ba.toUByteArray().usePinned { pinned ->
            fwrite(pinned.addressOf(0), 1, ba.size.toSizeT(), file)
        }
        fclose(file)
    }

    override fun create() {
        fclose(fopen(this.path, "wb") ?: throw FileAccessError(this.path))
    }
}

actual class TextFile actual constructor(val path: String) : FileLike.Text {
    override fun readAll(): String {
        val file = fopen(this.path, "rt") ?: throw FileAccessError(this.path)
        return commonReadAll(file).toKString().also { fclose(file) }
    }

    override fun write(s: String) {
        val file = fopen(this.path, "at") ?: throw FileAccessError(this.path)
        nativeWrite(s.cstr, strlen(s).toInt(), stdout)
        fclose(file)
    }

    override fun create() {
        fclose(fopen(this.path, "wt") ?: throw FileAccessError(this.path))
    }
}

actual object BinaryStd : FileLike.Binary {
    override fun readAll(): Array<UByte> {
        freopen(null, "rb", stdin)
        return commonReadAll(stdin!!).toUByteArray().toTypedArray()
    }

    override fun write(ba: Array<UByte>) {
        freopen(null, "wb", stdout)
        ba.toUByteArray().usePinned { pinned ->
            fwrite(pinned.addressOf(0), 1, ba.size.toSizeT(), stdout)
        }
    }

    override fun create() {}
}

actual object TextStd : FileLike.Text {
    override fun readAll(): String {
        freopen(null, "rt", stdin)
        return commonReadAll(stdin!!).toKString()
    }

    override fun write(s: String) {
        freopen(null, "wb", stdout)
        nativeWrite(s.cstr, strlen(s).toInt(), stdout)
    }

    override fun create() {}
}