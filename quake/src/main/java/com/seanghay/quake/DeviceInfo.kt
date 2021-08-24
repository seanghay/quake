package com.seanghay.quake

import android.content.Context
import android.os.Build

internal data class DeviceInfo(
  val model: String = Build.MODEL,
  val brand: String = Build.BRAND,
  val manufacturer: String = Build.MANUFACTURER,
  val sdk: Int = Build.VERSION.SDK_INT,
  val versionName: String,
  val versionCode: Long
) {

  companion object {

    @Suppress("Deprecation")
    fun from(context: Context): DeviceInfo {
      val pkg = context.packageManager.getPackageInfo(context.packageName, 0)
      val name = pkg.versionName

      val code = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) pkg.longVersionCode
      else pkg.versionCode.toLong()

      return DeviceInfo(
        versionCode = code,
        versionName = name
      )
    }

  }
}
