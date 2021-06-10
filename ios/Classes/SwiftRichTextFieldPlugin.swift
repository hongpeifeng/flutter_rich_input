//
//  YYTextFieldPlugin.swift
//  Runner
//
//  Created by lionel.hong on 2021/5/11.
//

import Foundation
import Flutter

public class SwiftRichTextFieldPlugin : NSObject,FlutterPlugin{
    
    public class func register(with registrar: FlutterPluginRegistrar) {
        registrar.register(RichTextFieldFactory.init(messenger: registrar.messenger()), withId: "com.fanbook.rich_textfield")
    }
    
}


