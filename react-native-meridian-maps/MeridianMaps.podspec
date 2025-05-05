require "json"

package = JSON.parse(File.read(File.join(__dir__, "package.json")))

Pod::Spec.new do |s|
  s.name                     = "MeridianMaps"
  s.version                  = package["version"]
  s.summary                  = package["description"]
  s.homepage                 = package["homepage"]
  s.license                  = package["license"]
  s.author                   = package["author"]
  s.ios.vendored_frameworks  = "ios/Meridian.xcframework"

  s.platform                 = :ios, "15.1"

  s.source                   = { :git => "https://github.com/gitamego/react-native-meridian-maps.git", :tag => "#{s.version}" }

  s.source_files              = "ios/**/*.{h,m,mm,cpp}"
  s.private_header_files      = "ios/**/*.h"
  s.header_mappings_dir       = "ios/Meridian.xcframework/ios-arm64/Meridian.framework/Headers"
  s.swift_version             = "5.0"

 install_modules_dependencies(s)
end
