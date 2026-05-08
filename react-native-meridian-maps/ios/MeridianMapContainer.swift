import Foundation
import UIKit
import Meridian

final class MeridianMapContainer: UIView {
  @objc dynamic var appId: String? { didSet { scheduleSync() } }
  @objc dynamic var mapId: String? { didSet { scheduleSync() } }
  @objc dynamic var appToken: String? { didSet { scheduleSync() } }
  @objc dynamic var placemarkID: String? { didSet { scheduleSync() } }

  // Tracks the placemarkID we've already pushed to the SDK, so we don't
  // re-select on every sync. nil means "no selection yet"; clearing the prop
  // back to nil intentionally won't trigger an explicit deselect (the SDK will
  // clear selection on map rebuild / mapKey swap).
  private var lastAppliedPlacemarkID: String?

  // RN dispatches prop setters one at a time within the same UI block, so a
  // synchronous didSet sees mismatched (newAppId, oldMapId, oldToken) on the
  // first setter to run. Coalesce all three into one runloop-tick sync.
  private var syncScheduled = false
  private func scheduleSync() {
    if syncScheduled { return }
    syncScheduled = true
    DispatchQueue.main.async { [weak self] in
      guard let self = self else { return }
      self.syncScheduled = false
      self.syncConfig()
      self.syncMap()
    }
  }

  private var mapVC: MRMapViewController?
  private let logTag = "MeridianMapContainer"

  override init(frame: CGRect) {
    super.init(frame: frame)
    backgroundColor = .systemBackground
    NSLog("%@: init frame=%@", logTag, NSCoder.string(for: frame))
  }

  required init?(coder: NSCoder) {
    super.init(coder: coder)
  }

  override func layoutSubviews() {
    super.layoutSubviews()
    if let vcView = mapVC?.view {
      vcView.frame = bounds
    }
  }

  override func didMoveToWindow() {
    super.didMoveToWindow()
    if window != nil {
      syncConfig()
      syncMap()
    }
  }

  private func syncConfig() {
    guard let token = appToken, !token.isEmpty else { return }
    MeridianSDKBootstrap.configure(token: token)
  }

  private func syncMap() {
    guard
      let appId = appId, !appId.isEmpty,
      let mapId = mapId, !mapId.isEmpty,
      MeridianSDKBootstrap.isConfigured()
    else { return }

    let newKey = MREditorKey(forMap: mapId, app: appId)

    if let vc = mapVC {
      let currentAppId = vc.mapView.mapKey.parent?.identifier
      let newAppId = newKey.parent?.identifier
      let sameMap = sameMapKey(vc.mapView.mapKey, newKey)
      let sameLocation = currentAppId == newAppId
      let placemarkChanged = placemarkID != lastAppliedPlacemarkID

      if sameMap && !placemarkChanged { return }

      // Mirror Android: any change to placemarkID rebuilds the VC so the
      // SDK's clean init path (initWithEditorKey:placemarkID:) does the
      // work — avoids manually juggling selectAnnotation/setVisibleMapRect
      // state. Cross-location swaps also require a rebuild (setMapKey
      // rejects keys whose parent location differs).
      if !sameLocation || placemarkChanged {
        NSLog("%@: rebuilding map VC (sameLocation=%d placemarkChanged=%d)",
              logTag, sameLocation, placemarkChanged)
        tearDownMapVC()
        buildMap(with: newKey)
        lastAppliedPlacemarkID = placemarkID
      } else {
        vc.mapView.mapKey = newKey
        NSLog("%@: mapKey swapped to map=%@ app=%@", logTag, mapId, appId)
      }
      return
    }

    buildMap(with: newKey)
    lastAppliedPlacemarkID = placemarkID
  }

  private func tearDownMapVC() {
    guard let vc = mapVC else { return }
    vc.willMove(toParent: nil)
    vc.view.removeFromSuperview()
    vc.removeFromParent()
    mapVC = nil
  }

  private func sameMapKey(_ a: MREditorKey?, _ b: MREditorKey) -> Bool {
    guard let a = a else { return false }
    return a.identifier == b.identifier && a.parent?.identifier == b.parent?.identifier
  }

  private func buildMap(with key: MREditorKey) {
    // Prefer the placemarkID-aware initializer when we have one, so the SDK
    // can render the map already zoomed to the placemark with its callout
    // open. Falls back to the plain init when no preselection is configured.
    let pid = placemarkID
    let vc: MRMapViewController
    if let pid = pid, !pid.isEmpty,
       let preselected = MRMapViewController(editorKey: key, placemarkID: pid) {
      vc = preselected
    } else {
      vc = MRMapViewController()
      vc.mapView.mapKey = key
    }
    vc.displaysSearchSheet(true,
                           withQuickSearchPlacemarks: [
                             MRDMapIconType.ATM.rawValue,
                             MRDMapIconType.waterFountain.rawValue
                           ],
                           hideDefaultQuickSearchIcons: false)
    self.mapVC = vc

    guard let childView = vc.view else { return }
    childView.frame = bounds
    childView.autoresizingMask = [UIView.AutoresizingMask.flexibleWidth, UIView.AutoresizingMask.flexibleHeight]
    childView.isHidden = false
    childView.alpha = 1

    attachController(vc, childView: childView, mapKey: key)
  }

}

private extension MeridianMapContainer {
  // Cap the retry budget. Total wait at 50ms × 60 = 3s, matching the upper
  // bound RN UIManager observes for new view hosts to be attached.
  static let attachMaxAttempts = 60

  func attachController(_ vc: MRMapViewController, childView: UIView, mapKey: MREditorKey, attempt: Int = 0) {
    if let parent = findParentViewController() {
      parent.addChild(vc)
      addSubview(childView)
      vc.didMove(toParent: parent)
      NSLog("%@: attached map VC as child of %@", logTag, String(describing: parent))
      return
    }
    if attempt >= Self.attachMaxAttempts {
      NSLog("%@: parent VC never appeared after %d attempts", logTag, attempt)
      return
    }
    DispatchQueue.main.asyncAfter(deadline: .now() + 0.05) { [weak self] in
      guard let self = self, let currentVC = self.mapVC, let currentView = currentVC.view else { return }
      self.attachController(currentVC, childView: currentView, mapKey: mapKey, attempt: attempt + 1)
    }
  }

  func findParentViewController() -> UIViewController? {
    var responder: UIResponder? = self
    while let current = responder {
      if let vc = current as? UIViewController { return vc }
      responder = current.next
    }
    return nil
  }
}
