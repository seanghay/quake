package com.seanghay.quake
import java.io.ByteArrayOutputStream
import java.io.File
import java.nio.charset.StandardCharsets.UTF_8
import java.util.zip.GZIPInputStream
import java.util.zip.GZIPOutputStream

internal fun gzip(content: String): ByteArray {
  val bos = ByteArrayOutputStream()
  GZIPOutputStream(bos).bufferedWriter(UTF_8).use { it.write(content) }
  return bos.toByteArray()
}

