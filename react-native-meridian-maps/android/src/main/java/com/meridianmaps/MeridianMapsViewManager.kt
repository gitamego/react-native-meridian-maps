package com.meridianmaps

import com.facebook.react.uimanager.SimpleViewManager
import com.facebook.react.uimanager.ThemedReactContext
import com.facebook.react.uimanager.annotations.ReactProp

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

  @ReactProp(name = "placemarkID")
  fun setPlacemarkID(view: MeridianMapContainer, value: String?) { view.setPlacemarkID(value) }
}
