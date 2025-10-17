import Foundation
import React
import Meridian

@objc(MeridianMapViewManager)
final class MeridianMapViewManager: RCTViewManager {
  override static func requiresMainQueueSetup() -> Bool { true }
  override func view() -> UIView! { MeridianMapContainer() }

  // Props
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

  // Command
  @objc func startRoute(_ reactTag: NSNumber, placemarkID: NSString) {
    attemptStartRoute(reactTag, placemarkID: placemarkID, attempt: 0)
  }

  private func attemptStartRoute(_ reactTag: NSNumber, placemarkID: NSString, attempt: Int) {
    bridge.uiManager.addUIBlock { [weak self] _, registry in
      guard let self = self else { return }
      let logTag = "MerMapsss"

      let uiView = self.bridge.uiManager?.view(forReactTag: reactTag)
      let v = (uiView as? MeridianMapContainer) ?? (registry?[reactTag] as? MeridianMapContainer)
      guard let container = v else {
        if attempt < 8 {
          let delay = 0.15 * pow(1.8, Double(attempt))
          DispatchQueue.main.asyncAfter(deadline: .now() + delay) {
            self.attemptStartRoute(reactTag, placemarkID: placemarkID, attempt: attempt + 1)
          }
        } else {
          // Give up after max attempts
        }
        return
      }
      guard let mapVC = container.mapVC else {
        if attempt < 8 {
          let delay = 0.15 * pow(1.8, Double(attempt))
          DispatchQueue.main.asyncAfter(deadline: .now() + delay) {
            self.attemptStartRoute(reactTag, placemarkID: placemarkID, attempt: attempt + 1)
          }
        } else {
          // Give up after max attempts
        }
        return
      }

      let mapView = mapVC.mapView
      let rawId = placemarkID as String

      if let local = mapView.placemarks.compactMap({ $0 as? MRPlacemark }).first(where: { pm in
        let pid = pm.key.identifier
        return pid == rawId
      }) {
        DispatchQueue.main.async { mapVC.startDirections(to: local) }
        NSLog("%@: startRoute local match -> started", logTag)
        return
      }

      let mapKey = mapView.mapKey
      let req = MRPlacemarkRequest(app: MREditorKey(identifier: container.appId ?? ""),
                                   placemarkIdentifier: rawId,
                                   mapKey: mapKey)
      req.start { resp, _ in
        if let place = resp?.getPlacemarks().first {
          DispatchQueue.main.async { mapVC.startDirections(to: place) }
          NSLog("%@: startRoute network match -> started", logTag)
        } else {
          NSLog("%@: startRoute no match via network for id=%@", logTag, rawId)
        }
      }
    }
  }
}
