import Foundation
import Meridian
import React

@objc(MeridianMapsModule)
final class MeridianMapsModule: NSObject, MRLocationManagerDelegate, RCTInvalidating {
  private static let logTag = "MeridianMapsModule"

  // Held for the module lifetime so beacon ranging keeps running until
  // stopWarmup() is called. Keyed by appId so a second warmup with a different
  // appId rebuilds the manager.
  private var locationManager: MRLocationManager?
  private var managerAppId: String?

  @objc static func requiresMainQueueSetup() -> Bool { false }

  // CoreLocation / MRLocationManager / Meridian.configure() all expect the
  // main thread. RN dispatches @objc methods on a per-module background queue
  // by default, so route every method here to main. Cheap (just a runloop
  // hop), and keeps the method bodies free of explicit DispatchQueue dances.
  @objc var methodQueue: DispatchQueue { DispatchQueue.main }

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

  // RCTInvalidating: fires on bridge teardown (reload / shutdown). Without
  // this the manager keeps ranging across JS reloads in dev and may outlive
  // the module instance on shutdown.
  @objc func invalidate() {
    locationManager?.stopUpdatingLocation()
    locationManager = nil
    managerAppId = nil
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
