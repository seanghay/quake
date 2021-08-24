package com.seanghay.quake

import android.graphics.Bitmap
import android.graphics.Color
import android.util.Base64
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter

internal fun encodeTextAsBitmap(byteArray: ByteArray, size: Int): Bitmap {
  val base64 = Base64.encode(byteArray, Base64.DEFAULT).toString(Charsets.UTF_8)
  val matrix = MultiFormatWriter()
    .encode(base64, BarcodeFormat.QR_CODE, size, size)

  val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
  repeat(size) { x ->
    repeat(size) { y ->
      bitmap.setPixel(x, y, if (matrix.get(x, y)) Color.BLACK else Color.WHITE)
    }
  }
  return bitmap
}