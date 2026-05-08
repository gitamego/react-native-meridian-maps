package com.meridianmaps

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log
import com.arubanetworks.meridian.Meridian

// Centralizes the one-shot `Meridian.configure()` call so the view-mount path
// and the JS-warmup module path don't both try to configure independently.
//
// Notes:
// - `Meridian.configure()` on Android expects the main thread (the SDK's
//   internal handlers post to Looper.getMainLooper()). Forcing main-thread
//   here so callers don't need to think about it.
// - If `getShared()` works, the SDK is already configured. We use that as a
//   self-healing path in case our `configured` flag desynced (e.g. caller
//   crashed mid-configure on a prior run).
object MeridianSdkBootstrap {
  private const val TAG = "MeridianSdkBootstrap"

  @Volatile private var configured: Boolean = false
  private val mainHandler = Handler(Looper.getMainLooper())

  fun isConfigured(): Boolean = configured

  fun configure(context: Context, token: String): Boolean {
    if (configured) return true
    if (token.isBlank()) return false

    if (Looper.myLooper() == Looper.getMainLooper()) {
      return configureOnMain(context, token)
    }
    // Block the calling thread until configure runs on main. We could make
    // this async, but every current caller (module + view) needs the result
    // synchronously to decide what to do next.
    val lock = Object()
    var result = false
    var done = false
    mainHandler.post {
      result = configureOnMain(context, token)
      synchronized(lock) {
        done = true
        @Suppress("PLATFORM_CLASS_MAPPED_TO_KOTLIN")
        (lock as java.lang.Object).notifyAll()
      }
    }
    synchronized(lock) {
      while (!done) {
        try {
          @Suppress("PLATFORM_CLASS_MAPPED_TO_KOTLIN")
          (lock as java.lang.Object).wait(5000)
          if (!done) {
            Log.w(TAG, "configure(): main-thread post timed out")
            return false
          }
        } catch (ie: InterruptedException) {
          Thread.currentThread().interrupt()
          return false
        }
      }
    }
    return result
  }

  @Synchronized
  private fun configureOnMain(context: Context, token: String): Boolean {
    if (configured) return true
    try {
      Meridian.configure(context.applicationContext, token)
    } catch (e: Throwable) {
      Log.w(TAG, "Meridian.configure threw; checking whether SDK is already up", e)
    }
    return try {
      Meridian.getShared().supportDarkTheme(true)
      configured = true
      true
    } catch (e: Throwable) {
      Log.w(TAG, "Meridian.getShared() failed; SDK is not configured", e)
      false
    }
  }
}
