import Foundation
import Meridian
import React

@objc(MeridianMapsModule)
final class MeridianMapsModule: NSObject, MRLocationManagerDelegate {
  private static let logTag = "MeridianMapsModule"

  // Held for the module lifetime so beacon ranging keeps running until
  // stopWarmup() is called. Keyed by appId so a second warmup with a different
  // appId rebuilds the manager.
  private var locationManager: MRLocationManager?
  private var managerAppId: String?

  @objc static func requiresMainQueueSetup() -> Bool { false }

  @objc func warmupLocation(_ appToken: NSString,
                            appId: NSString,
                            resolver resolve: @escaping RCTPromiseResolveBlock,
                            rejecter reject: @escaping RCTPromiseRejectBlock) {
    let token = appToken as String
    let app = appId as String
    guard !token.isEmpty, !app.isEmpty else {
      reject("MERIDIAN_WARMUP_BAD_ARGS", "appToken and appId are required", nil)
      return
    }
    if !MeridianSDKBootstrap.configure(token: token) {
      reject("MERIDIAN_CONFIGURE_FAILED", "Meridian.configure failed; verify appToken", nil)
      return
    }
    if locationManager != nil, managerAppId == app {
      resolve(nil)
      return
    }
    locationManager?.stopUpdatingLocation()
    let mgr = MRLocationManager(app: MREditorKey(identifier: app))
    mgr.delegate = self
    mgr.startUpdatingLocation()
    locationManager = mgr
    managerAppId = app
    NSLog("%@: warmup started for app=%@", Self.logTag, app)
    resolve(nil)
  }

  @objc func stopWarmup(_ resolve: @escaping RCTPromiseResolveBlock,
                        rejecter reject: @escaping RCTPromiseRejectBlock) {
    locationManager?.stopUpdatingLocation()
    locationManager = nil
    managerAppId = nil
    NSLog("%@: warmup stopped", Self.logTag)
    resolve(nil)
  }

  // MARK: - MRLocationManagerDelegate

  func locationManager(_ manager: MRLocationManager, didUpdateTo location: MRLocation) {
    NSLog("%@: warmup didUpdateToLocation map=%@",
          Self.logTag, location.mapKey.identifier)
  }

  func locationManager(_ manager: MRLocationManager, didFailWithError error: Error) {
    NSLog("%@: warmup didFailWithError %@",
          Self.logTag, error.localizedDescription)
  }
}
