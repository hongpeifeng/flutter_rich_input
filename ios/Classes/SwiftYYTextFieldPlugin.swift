//
//  YYTextFieldPlugin.swift
//  Runner
//
//  Created by lionel.hong on 2021/5/11.
//

import Foundation
import Flutter

public class SwiftYYTextFieldPlugin : NSObject,FlutterPlugin{
    
    public class func register(with registrar: FlutterPluginRegistrar) {
        registrar.register(YYTextFieldFactory.init(messenger: registrar.messenger()), withId: "com.fanbook.yytextfield")
    }
    
}


