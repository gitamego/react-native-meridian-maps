import Foundation
import React

@objc(MeridianMapViewManager)
final class MeridianMapViewManager: RCTViewManager {
  override static func requiresMainQueueSetup() -> Bool { true }
  override func view() -> UIView! { MeridianMapContainer() }

  @objc func setAppId(_ reactTag: NSNumber, value: NSString) {
    bridge.uiManager.addUIBlock { _, registry in
      (registry?[reactTag] as? MeridianMapContainer)?.appId = value as String
    }
  }

  @objc func setMapId(_ reactTag: NSNumber, value: NSString) {
    bridge.uiManager.addUIBlock { _, registry in
      (registry?[reactTag] as? MeridianMapContainer)?.mapId = value as String
    }
  }

  @objc func setAppToken(_ reactTag: NSNumber, value: NSString) {
    bridge.uiManager.addUIBlock { _, registry in
      (registry?[reactTag] as? MeridianMapContainer)?.appToken = value as String
    }
  }
}
