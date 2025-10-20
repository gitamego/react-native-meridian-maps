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
    }
  }

  override func didMoveToWindow() {
    super.didMoveToWindow()
    if window != nil {
      tryConfigure()
    }
  }

  private func tryConfigure() {
    if mapVC != nil {
      return
    }
    guard let appId = appId, let mapId = mapId, let appToken = appToken else {
      return
    }

    let cfg = MRConfig()
    cfg.applicationToken = appToken
    Meridian.configure(cfg)

    let mapKey = MREditorKey(forMap: mapId, app: appId)
    let vc = MRMapViewController()
    vc.mapView.mapKey = mapKey

    vc.displaysSearchSheet(true,
                           withQuickSearchPlacemarks: [
                             MRDMapIconType.ATM.rawValue,
                             MRDMapIconType.waterFountain.rawValue
                           ],
                           hideDefaultQuickSearchIcons: false)
    self.mapVC = vc

    guard let childView = vc.view else {
      return
    }
    childView.frame = bounds
    childView.autoresizingMask = [UIView.AutoresizingMask.flexibleWidth, UIView.AutoresizingMask.flexibleHeight]
    childView.isHidden = false
    childView.alpha = 1

    attachController(vc, childView: childView)
  }
}

private extension MeridianMapContainer {
  func attachController(_ vc: MRMapViewController, childView: UIView) {
    if let parent = findParentViewController() {
      parent.addChild(vc)
      addSubview(childView)
      vc.didMove(toParent: parent)
      NSLog("%@: attached map VC as child of %@", logTag, String(describing: parent))
      return
    }

    NSLog("%@: parent VC not ready, retrying attach...", logTag)
    DispatchQueue.main.asyncAfter(deadline: .now() + 0.05) { [weak self] in
      guard let self = self, let currentVC = self.mapVC, let currentView = currentVC.view else { return }
      self.attachController(currentVC, childView: currentView)
    }
  }
}

