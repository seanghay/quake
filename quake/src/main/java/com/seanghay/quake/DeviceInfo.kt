package com.seanghay.quake

import android.content.Context
import android.os.Build

internal data class DeviceInfo(
  val model: String = android.os.Build.MODEL,
  val brand: String = android.os.Build.BRAND,
  val manufacturer: String = android.os.Build.MANUFACTURER,
  val sdk: Int = android.os.Build.VERSION.SDK_INT,
  val versionName: String,
  val versionCode: Long
) {
  companion object {

    fun from(context: Context): DeviceInfo {
      val pkg = context.packageManager.getPackageInfo(context.packageName, 0)
      val name = pkg.versionName
      val code = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
        pkg.longVersionCode
      } else {
        pkg.versionCode.toLong()
      }

      return DeviceInfo(
        versionCode = code,
        versionName = name
      )
    }
  }
}
