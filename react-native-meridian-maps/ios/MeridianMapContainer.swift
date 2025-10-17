import Foundation
import UIKit
import Meridian
import React

final class MeridianMapContainer: UIView {
  @objc dynamic var appId: String? { didSet { tryConfigure() } }
  @objc dynamic var mapId: String? { didSet { tryConfigure() } }
  @objc dynamic var appToken: String? { didSet { tryConfigure() } }

  private(set) var mapVC: MRMapViewController?
  private let logTag = "MerMapsss"

  // Find nearest parent view controller to properly embed MRMapViewController
  private func findParentViewController() -> UIViewController? {
    var responder: UIResponder? = self
    while let current = responder {
      if let vc = current as? UIViewController { return vc }
      responder = current.next
    }
    return nil
  }

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
      NSLog("%@: layoutSubviews mapVC.view=%@ bounds=%@", logTag, NSCoder.string(for: vcView.frame), NSCoder.string(for: bounds))
    } else {
      NSLog("%@: layoutSubviews mapVC.view=nil bounds=%@", logTag, NSCoder.string(for: bounds))
    }
  }

  override func didMoveToWindow() {
    super.didMoveToWindow()
    NSLog("%@: didMoveToWindow window?=%@ bounds=%@", logTag, window != nil ? "yes" : "no", NSCoder.string(for: bounds))
    if window != nil {
      tryConfigure()
    }
  }

  private func tryConfigure() {
    if mapVC != nil {
      NSLog("%@: tryConfigure: already has mapVC", logTag)
      return
    }
    guard let appId = appId, let mapId = mapId, let appToken = appToken else {
      NSLog("%@: tryConfigure: missing props appId=%@ mapId=%@ token?=%@", logTag, String(describing: self.appId), String(describing: self.mapId), self.appToken != nil ? "yes" : "no")
      return
    }
    NSLog("%@: tryConfigure: appId=%@ mapId=%@ token=%@ size=%@ window?=%@", logTag, appId, mapId, (appToken as NSString).substring(to: min(8, appToken.count)), NSCoder.string(for: bounds), window != nil ? "yes" : "no")

    let cfg = MRConfig()
    cfg.applicationToken = appToken
    Meridian.configure(cfg)
    NSLog("%@: Meridian.configure done", logTag)

    let mapKey = MREditorKey(forMap: mapId, app: appId)
    let vc = MRMapViewController()
    vc.mapView.mapKey = mapKey
    // Configure search sheet immediately upon controller creation
    vc.displaysSearchSheet(true,
                           withQuickSearchPlacemarks: [
                             MRDMapIconType.ATM.rawValue,
                             MRDMapIconType.waterFountain.rawValue
                           ],
                           hideDefaultQuickSearchIcons: false)
    // vc.displaysSearchSheet = true
    self.mapVC = vc

    guard let childView = vc.view else {
      NSLog("%@: mapVC.view is nil after init", logTag)
      return
    }
    childView.frame = bounds
    childView.autoresizingMask = [UIView.AutoresizingMask.flexibleWidth, UIView.AutoresizingMask.flexibleHeight]
    childView.isHidden = false
    childView.alpha = 1

    attachController(vc, childView: childView)

    // Ensure presentation after attachment
    DispatchQueue.main.async { [weak vc] in
      vc?.displaysSearchSheet(true,
                              withQuickSearchPlacemarks: [
                                MRDMapIconType.ATM.rawValue,
                                MRDMapIconType.waterFountain.rawValue
                              ],
                              hideDefaultQuickSearchIcons: false)
    }
    DispatchQueue.main.asyncAfter(deadline: .now() + 2.0) { [weak vc] in
      vc?.displaysSearchSheet(true,
                              withQuickSearchPlacemarks: [
                                MRDMapIconType.ATM.rawValue,
                                MRDMapIconType.waterFountain.rawValue
                              ],
                              hideDefaultQuickSearchIcons: false)
    }
  }
}

private extension MeridianMapContainer {
  func attachController(_ vc: MRMapViewController, childView: UIView) {
    // Ensure we only attach to the actual parent view controller that owns this view
    if let parent = findParentViewController() {
      parent.addChild(vc)
      addSubview(childView)
      vc.didMove(toParent: parent)
      NSLog("%@: attached map VC as child of %@", logTag, String(describing: parent))
      return
    }

    // Parent not ready yet, retry shortly on the next runloop tick
    NSLog("%@: parent VC not ready, retrying attach...", logTag)
    DispatchQueue.main.asyncAfter(deadline: .now() + 0.05) { [weak self] in
      guard let self = self, let currentVC = self.mapVC, let currentView = currentVC.view else { return }
      self.attachController(currentVC, childView: currentView)
    }
  }
}

