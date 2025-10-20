package com.meridianmaps

import com.facebook.react.bridge.ReadableArray
import com.facebook.react.common.MapBuilder
import com.facebook.react.uimanager.SimpleViewManager
import com.facebook.react.uimanager.ThemedReactContext
import com.facebook.react.uimanager.annotations.ReactProp
import android.util.Log

class MeridianMapsViewManager : SimpleViewManager<MeridianMapContainer>() {
  override fun getName() = "MeridianMapView"

  override fun createViewInstance(reactContext: ThemedReactContext): MeridianMapContainer {
    return MeridianMapContainer(reactContext)
  }

  @ReactProp(name = "appId")
  fun setAppId(view: MeridianMapContainer, value: String?) { view.setAppId(value) }

  @ReactProp(name = "mapId")
  fun setMapId(view: MeridianMapContainer, value: String?) { view.setMapId(value) }

  @ReactProp(name = "appToken")
  fun setAppToken(view: MeridianMapContainer, value: String?) { view.setAppToken(value) }

  override fun getCommandsMap(): MutableMap<String, Int> = MapBuilder.of("startRoute", 1)

  override fun receiveCommand(view: MeridianMapContainer, commandId: Int, args: ReadableArray?) {
    if (commandId == 1) {
      val id = args?.getString(0) ?: return
      view.startRoute(id)
    }
  }
}