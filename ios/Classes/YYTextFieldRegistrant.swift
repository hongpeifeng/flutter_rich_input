//
//  YYTextFieldRegistrant.swift
//  flutter_text_field
//
//  Created by lionel.hong on 2021/5/11.
//

import Foundation
import Flutter


class YYTextFieldRegistrant {
    class func register(with registry: FlutterPluginRegistry) {
        SwiftYYTextFieldPlugin.register(with: registry.registrar(forPlugin: "YYTextFieldPlugin")!)
    }
}
