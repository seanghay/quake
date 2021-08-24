package com.seanghay.quake

import android.content.Context
import androidx.startup.Initializer

@Suppress("Unused")
class QuakeInitializer : Initializer<Quake> {

  override fun create(context: Context): Quake {
    return Quake(context)
  }

  override fun dependencies(): List<Class<out Initializer<*>>> {
    return emptyList()
  }
}