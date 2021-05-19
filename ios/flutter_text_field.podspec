


Pod::Spec.new do |s|
  s.name             = 'flutter_text_field'
  s.version          = '0.0.1'
  s.summary          = 'iOS TextField'
  s.description      = <<-DESC
iOS TextField
                       DESC
  s.homepage         = 'http://example.com'
  s.license          = { :file => '../LICENSE' }
  s.author           = { 'Your Company' => 'email@example.com' }
  s.source           = { :path => '.' }
  s.source_files = 'Classes/**/*'

  s.dependency 'Flutter'
  s.dependency 'YYText'
  s.swift_version = '5.0'

  s.platform = :ios, '9.0'
  
end
