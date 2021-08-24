package com.seanghay.quake

import android.app.Activity
import android.app.Application
import android.app.Dialog
import android.content.Context
import android.hardware.SensorManager
import android.widget.ImageView
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import com.google.gson.Gson
import com.squareup.seismic.ShakeDetector
import java.util.*
import java.util.concurrent.Executors
import kotlin.math.roundToInt

class Quake(private val context: Context) {

  private data class ClassDescriptor(
    val name: String,
    val simpleName: String,
    val type: String,
    val children: List<ClassDescriptor>
  )

  private data class Report(
    val device: DeviceInfo,
    val activity: ClassDescriptor
  )

  private val gson = Gson()
  private val executor = Executors.newSingleThreadExecutor()
  private val sensorManager = ContextCompat.getSystemService(context, SensorManager::class.java)!!
  private val callbacks = QuakeActivityCallbacks(this)
  private val storage = WeakHashMap<Activity, ShakeDetector>()
  private val dialogs = WeakHashMap<Activity, Dialog>()

  init {
    val application = context.applicationContext as? Application
    application?.registerActivityLifecycleCallbacks(callbacks)
  }

  private fun showInfo(activity: Activity) {
    val descriptor = describeActivity(activity) ?: return

    val report = Report(
      device = DeviceInfo.from(activity),
      activity = descriptor
    )

    executor.execute {

      val compressed = gzip(gson.toJson(report))
      val size = activity.resources.displayMetrics.widthPixels * .75
      val bitmap = encodeTextAsBitmap(compressed, size.roundToInt())

      activity.runOnUiThread {
        if (!activity.isDestroyed || !activity.isFinishing) {

          dialogs[activity]?.dismiss()
          dialogs.remove(activity)

          val dialog = AlertDialog.Builder(activity)
            .setTitle(DIALOG_TITLE)
            .setMessage(DIALOG_MESSAGE)
            .setView(ImageView(activity).apply {
              setImageBitmap(bitmap)
            })
            .setPositiveButton(DIALOG_BUTTON) {d, _ -> d.dismiss()}
            .create()

          dialog.setOnDismissListener { dialogs.remove(activity) }
          dialogs[activity] = dialog
          dialog.show()
        }
      }
    }
  }

  private fun describeActivity(activity: Activity): ClassDescriptor? {
    if (activity !is FragmentActivity) return null
    val simpleName = activity.javaClass.simpleName
    val name = activity.javaClass.name
    return ClassDescriptor(
      name,
      simpleName,
      ACTIVITY_TYPE,
      allFragmentsOf(activity.supportFragmentManager)
    )
  }

  private fun allFragmentsOf(fm: FragmentManager): List<ClassDescriptor> {
    if (fm.fragments.isEmpty()) return emptyList()
    val collection = arrayListOf<ClassDescriptor>()
    for (fragment in fm.fragments) {
      val descriptor = ClassDescriptor(
        simpleName = fragment.javaClass.simpleName,
        name = fragment.javaClass.name,
        type = FRAGMENT_TYPE,
        children = allFragmentsOf(fragment.childFragmentManager)
      )
      collection.add(descriptor)
    }
    return collection
  }

  internal fun start(activity: Activity) {
    val cached = storage[activity]
    if (cached == null) {
      val callback = ShakeDetector.Listener { showInfo(activity) }
      val detector = ShakeDetector(callback)
      storage[activity] = detector
      detector.start(sensorManager)
      return
    }
    cached.start(sensorManager)
  }

  internal fun stop(activity: Activity) {
    val cached = storage[activity] ?: return
    cached.stop()
    storage.remove(activity)
  }

  companion object {
    private const val FRAGMENT_TYPE = "fragment"
    private const val ACTIVITY_TYPE = "activity"
    private const val DIALOG_TITLE = "Quake QR"
    private const val DIALOG_MESSAGE = "Share this QR with the developer"
    private const val DIALOG_BUTTON = "Done"
  }
}