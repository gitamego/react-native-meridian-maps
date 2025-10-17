require "json"

package = JSON.parse(File.read(File.join(__dir__, "package.json")))

Pod::Spec.new do |s|
  s.name                     = "MeridianMaps"
  s.version                  = package["version"]
  s.summary                  = "React Native wrapper for Meridian Maps SDK"
  s.description              = package["description"]
  s.homepage                 = package["homepage"]
  s.license                  = package["license"]
  s.author                   = package["author"]
  # Use CocoaPods Meridian SDK instead of a vendored XCFramework
  s.dependency               'MeridianSDK', '~> 11.3.0'

  s.platform                 = :ios, "16"

  s.source                   = { :git => "https://github.com/gitamego/react-native-meridian-maps.git", :tag => "#{s.version}" }

  s.source_files              = "ios/**/*.{h,m,mm,cpp,swift}"
  s.private_header_files      = "ios/**/*.h"
  s.swift_version             = "5.0"

 install_modules_dependencies(s)
end

