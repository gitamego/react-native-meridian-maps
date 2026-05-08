import Foundation
import Meridian

// Centralizes the one-shot `Meridian.configure(_:)` call so the view-mount
// path and the JS-warmup module path share the same idempotency guard. The
// iOS SDK rejects a second `configure()` call, so this state is process-wide.
enum MeridianSDKBootstrap {
  private static var didConfigure = false

  static func isConfigured() -> Bool { didConfigure }

  @discardableResult
  static func configure(token: String) -> Bool {
    guard !token.isEmpty else { return false }
    if didConfigure {
      if Meridian.sharedConfig()?.applicationToken != token {
        Meridian.sharedConfig()?.applicationToken = token
      }
      return true
    }
    let cfg = MRConfig()
    cfg.applicationToken = token
    Meridian.configure(cfg)
    didConfigure = true
    return true
  }
}
