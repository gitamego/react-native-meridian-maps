package com.meridianmaps

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.widget.FrameLayout
import android.os.Handler
import android.os.Looper
import android.view.Choreographer
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import com.arubanetworks.meridian.Meridian
import com.arubanetworks.meridian.editor.EditorKey
import com.arubanetworks.meridian.maps.MapSheetFragment
import com.arubanetworks.meridian.maps.directions.DirectionsDestination
import com.facebook.react.bridge.ReactContext

class MeridianMapContainer @JvmOverloads constructor(
  context: Context,
  attrs: AttributeSet? = null,
  defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

  private var appId: String? = null
  private var mapId: String? = null
  private var appToken: String? = null

  private var fragment: MapSheetFragment? = null
  private val containerId: Int = View.generateViewId()
  private var pendingLoad: Boolean = false
  private var testButtonAdded: Boolean = false
  private var globalLayoutLogCount: Int = 0
  private val uiHandler = Handler(Looper.getMainLooper())
  val TAG = "MeridianMapContainer"

  init {
    val container = FrameLayout(context)
    container.id = containerId
    addView(container, LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT))

    addOnAttachStateChangeListener(object : OnAttachStateChangeListener {
      override fun onViewAttachedToWindow(v: View) {
        if (pendingLoad) tryLoad()
      }
      override fun onViewDetachedFromWindow(v: View) {}
    })
  }

  fun setAppId(value: String?) {
    appId = value
    tryLoad()
  }

  fun setMapId(value: String?) {
    mapId = value
    tryLoad()
  }

  fun setAppToken(value: String?) {
    appToken = value
    tryLoad()
  }

  private fun tryLoad() {
    val a = appId
    val m = mapId
    val t = appToken
    if (a.isNullOrBlank() || m.isNullOrBlank() || t.isNullOrBlank()) return
    try {
      if (!isConfigured) {
        Meridian.configure(context.applicationContext, t)
        Meridian.getShared().supportDarkTheme(true)
        isConfigured = true
      }
    } catch (e: Throwable) {
      Log.w(TAG, "configure once: ${e.message}")
    }
    if (fragment != null) return
    if (!isAttachedToWindow) {
      pendingLoad = true
      return
    }

    post { attachFragment(a!!, m!!) }
  }

  private fun fragmentManager(): FragmentManager? {
    val activity = (context as? ReactContext)?.currentActivity
    return if (activity is FragmentActivity) activity.supportFragmentManager else null
  }

  private fun attachFragment(appId: String, mapId: String) {
    val fm = fragmentManager() ?: run {
      return
    }
    val appKey = EditorKey.forApp(appId)
    val mapKey = EditorKey.forMap(mapId, appKey)
    val frag = MapSheetFragment.Builder().setMapKey(mapKey).build()
    try {
      fm.beginTransaction().replace(containerId, frag, "MeridianMapFragment").commitNowAllowingStateLoss()
      fragment = frag
      pendingLoad = false
      scheduleEnsureSized()
    } catch (e: Throwable) {
      Log.e(TAG, "attach failed: ${e.message}")
    }
  }

  private fun scheduleEnsureSized() {
    Choreographer.getInstance().postFrameCallback {
      val w = width
      val h = height
      val v = fragment?.view
      if (v != null) {
        v.layoutParams = v.layoutParams?.apply {
          width = LayoutParams.MATCH_PARENT
          height = LayoutParams.MATCH_PARENT
        } ?: LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
        v.measure(
          View.MeasureSpec.makeMeasureSpec(w, View.MeasureSpec.EXACTLY),
          View.MeasureSpec.makeMeasureSpec(h, View.MeasureSpec.EXACTLY)
        )
        v.layout(0, 0, w, h)
        v.requestLayout()
        Log.d(TAG, "ensureSized frame: fragView=${v.width}x${v.height} cont=${w}x${h}")
      } else {
        Log.d(TAG, "ensureSized frame: fragView=null cont=${w}x${h}")
      }
      uiHandler.postDelayed({ scheduleEnsureSized() }, 500)
    }
  }

  fun startRoute(placemarkId: String) {
    val frag = fragment ?: return
    Log.d(TAG, "teeest" + placemarkId)
    runCatching {
      val target = frag.mapView.placemarks.firstOrNull { it.key.id == placemarkId }
      if (target != null) frag.startDirections(DirectionsDestination.forPlacemarkKey(target.key))
    }.onFailure { e -> Log.e(TAG, "startRoute error: ${e.message}") }
  }

  override fun onDetachedFromWindow() {
    super.onDetachedFromWindow()
    fragment = null
  }

  companion object {
    @Volatile private var isConfigured: Boolean = false
  }
}


