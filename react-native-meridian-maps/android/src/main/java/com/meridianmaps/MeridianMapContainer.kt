package com.meridianmaps

import android.content.Context
import android.graphics.Matrix
import android.graphics.PointF
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.widget.FrameLayout
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import com.arubanetworks.meridian.editor.EditorKey
import com.arubanetworks.meridian.location.MeridianLocation
import com.arubanetworks.meridian.location.MeridianOrientation
import com.arubanetworks.meridian.maps.MapInfo
import com.arubanetworks.meridian.maps.MapSheetFragment
import com.arubanetworks.meridian.maps.MapView
import com.facebook.react.bridge.ReactContext


class MeridianMapContainer @JvmOverloads constructor(
  context: Context,
  attrs: AttributeSet? = null,
  defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

  private var appId: String? = null
  private var mapId: String? = null
  private var appToken: String? = null
  private var placemarkID: String? = null
  private var lastAppliedPlacemarkID: String? = null

  private var fragment: MapSheetFragment? = null
  private val containerId: Int = View.generateViewId()
  private var pendingLoad: Boolean = false

  private val measureAndLayout = Runnable {
    measure(
      MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY),
      MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY)
    )
    layout(left, top, right, bottom)
  }

  init {
    val container = FrameLayout(context)
    container.id = containerId
    addView(container, LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT))

    addOnAttachStateChangeListener(object : OnAttachStateChangeListener {
      override fun onViewAttachedToWindow(v: View) {
        if (pendingLoad) scheduleSync()
      }
      override fun onViewDetachedFromWindow(v: View) {}
    })
  }

  // RN dispatches prop setters one at a time, so a synchronous syncMap inside
  // setAppId() would see (newAppId, oldMapId, oldToken). Coalesce all three
  // setters into a single sync on the next runloop tick.
  private val syncRunnable = Runnable {
    syncScheduled = false
    ensureConfigured()
    syncMap()
  }
  private var syncScheduled = false
  private fun scheduleSync() {
    if (syncScheduled) return
    syncScheduled = true
    post(syncRunnable)
  }

  fun setAppId(value: String?) {
    appId = value
    scheduleSync()
  }

  fun setMapId(value: String?) {
    mapId = value
    scheduleSync()
  }

  fun setAppToken(value: String?) {
    appToken = value
    scheduleSync()
  }

  fun setPlacemarkID(value: String?) {
    placemarkID = value
    scheduleSync()
  }

  // React Native's UIManager calls requestLayout() on us when JS-side layout
  // changes the view's size, but RN does not propagate the layout pass to
  // children of native views. Re-run measure+layout once on the next frame so
  // the embedded Fragment view picks up the new size — bounded to a single
  // pass per request, NOT a polling loop.
  override fun requestLayout() {
    super.requestLayout()
    post(measureAndLayout)
  }

  override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
    super.onSizeChanged(w, h, oldw, oldh)
    val v = fragment?.view ?: return
    val lp = v.layoutParams ?: LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
    lp.width = LayoutParams.MATCH_PARENT
    lp.height = LayoutParams.MATCH_PARENT
    v.layoutParams = lp
    v.measure(
      MeasureSpec.makeMeasureSpec(w, MeasureSpec.EXACTLY),
      MeasureSpec.makeMeasureSpec(h, MeasureSpec.EXACTLY)
    )
    v.layout(0, 0, w, h)
  }

  private fun ensureConfigured() {
    val t = appToken ?: return
    MeridianSdkBootstrap.configure(context.applicationContext, t)
  }

  private fun syncMap() {
    val a = appId
    val m = mapId
    if (a.isNullOrBlank() || m.isNullOrBlank() || appToken.isNullOrBlank()) return
    ensureConfigured()
    if (!MeridianSdkBootstrap.isConfigured()) return

    if (!isAttachedToWindow) {
      pendingLoad = true
      return
    }

    val newKey = EditorKey.forMap(m, EditorKey.forApp(a))
    val frag = fragment
    val pid = placemarkID

    if (frag != null) {
      val mv = frag.mapView
      val current = mv?.mapKey
      val sameMap = current != null && current.id == newKey.id && current.parent?.id == a
      val sameLocation = current?.parent?.id == a
      val placemarkChanged = pid != lastAppliedPlacemarkID

      if (sameMap && !placemarkChanged) return

      // The Android SDK has no public runtime API for programmatic placemark
      // selection — only `MapSheetFragment.Builder.setPlacemarkId()` at
      // construction time. Any time the placemarkID prop changes (set,
      // changed, or cleared) we rebuild the fragment so the SDK's clean init
      // path is the one that does the work. Same when the location changes
      // (mv.setMapKey is scoped to a single location). Only smooth-swap via
      // setMapKey when nothing about the selection is changing.
      if (!sameLocation || placemarkChanged) {
        Log.d(TAG, "rebuilding fragment (location=$sameLocation placemarkChanged=$placemarkChanged)")
        attachFragment(newKey, pid)
        lastAppliedPlacemarkID = pid
      } else {
        try {
          mv?.setMapKey(newKey)
          Log.d(TAG, "mapKey swapped to map=$m app=$a")
        } catch (e: Throwable) {
          Log.w(TAG, "setMapKey failed, falling back to fragment replace: ${e.message}")
          attachFragment(newKey, pid)
          lastAppliedPlacemarkID = pid
        }
      }
      return
    }

    attachFragment(newKey, pid)
    lastAppliedPlacemarkID = pid
  }

  private fun fragmentManager(): FragmentManager? {
    val activity = (context as? ReactContext)?.currentActivity
    return if (activity is FragmentActivity) activity.supportFragmentManager else null
  }

  private fun attachFragment(mapKey: EditorKey, placemarkId: String? = null) {
    val fm = fragmentManager() ?: return
    val builder = MapSheetFragment.Builder().setMapKey(mapKey)
    if (!placemarkId.isNullOrBlank()) builder.setPlacemarkId(placemarkId)
    val frag = builder.build()
    try {
      fm.beginTransaction()
        .replace(containerId, frag, "MeridianMapFragment")
        .commitNowAllowingStateLoss()
      fragment = frag
      pendingLoad = false
      // Builder.setPlacemarkId() selects and opens the bottom sheet but does
      // NOT pan/zoom — unlike iOS's `initWithEditorKey:placemarkID:` which
      // does both. Hook MapView.MapEventListener.onPlacemarksLoadFinish()
      // which fires once placemark data is available, then pan.
      //
      // MapSheetFragment.setMapEventListener is a multiplexing setter (the
      // fragment stores the external listener separately and dispatches to
      // it in addition to its own internal handling — verified via javap),
      // so registering this does NOT break the fragment. We must not call
      // back into the fragment from inside these callbacks — that was the
      // cause of the recursion crash in earlier attempts.
      if (!placemarkId.isNullOrBlank()) {
        frag.setMapEventListener(makePanOnPlacemarksLoaded(placemarkId))
      }
    } catch (e: Throwable) {
      Log.e(TAG, "attach failed: ${e.message}")
    }
  }

  // Pan-to-placemark once the map is fully rendered. Builder.setPlacemarkId
  // selects + opens the bottom sheet but never pans on Android (unlike iOS's
  // initWithEditorKey:placemarkID:), and the SDK's own internal pan after
  // placemarks load can overwrite an early pan from us — so we wait until
  // onMapRenderFinish (last event in the chain) and use a one-shot flag so
  // we don't fight user-initiated pans on subsequent renders.
  private fun makePanOnPlacemarksLoaded(placemarkId: String): MapView.MapEventListener {
    var panned = false
    val tryPan = tryPan@{
      if (panned) return@tryPan
      val mv = fragment?.mapView ?: return@tryPan
      val placemark = mv.placemarks.firstOrNull { it.key.id == placemarkId } ?: return@tryPan
      runCatching {
        mv.scrollToRect(
          mv.rectWithCenter(PointF(placemark.x, placemark.y), MapInfo.ZoomLevel.ZOOM_LEVEL_LARGE_STORE),
          true
        )
        panned = true
        Log.d(TAG, "pan: zoomed to preselected $placemarkId")
      }.onFailure { Log.w(TAG, "pan to $placemarkId failed: ${it.message}") }
    }
    return object : MapView.MapEventListener {
      override fun onMapLoadStart() {}
      override fun onMapLoadFinish() {}
      override fun onPlacemarksLoadFinish() {}
      override fun onMapRenderFinish() { tryPan() }
      override fun onMapLoadFail(t: Throwable?) {
        Log.w(TAG, "MapEventListener.onMapLoadFail: ${t?.message}")
      }
      override fun onMapTransformChange(matrix: Matrix?) {}
      override fun onLocationUpdated(location: MeridianLocation?) {}
      override fun onOrientationUpdated(orientation: MeridianOrientation?) {}
    }
  }

  override fun onDetachedFromWindow() {
    super.onDetachedFromWindow()
    fragment = null
  }

  companion object {
    private const val TAG = "MeridianMapContainer"
  }
}
