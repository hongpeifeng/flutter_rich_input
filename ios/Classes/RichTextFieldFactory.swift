//
//  YYTextFieldFactory.swift
//  flutter_text_field
//
//  Created by lionel.hong on 2021/5/11.
//

import Foundation
import Flutter


class RichTextFieldFactory :NSObject,FlutterPlatformViewFactory{
    
    var messenger:FlutterBinaryMessenger!
    
    init(messenger:FlutterBinaryMessenger) {
        self.messenger = messenger
        super.init()
}
    
    func create(withFrame frame: CGRect, viewIdentifier viewId: Int64, arguments args: Any?) -> FlutterPlatformView {
        let ret = RichTextField(frame: frame, viewId: viewId, args: args, messenger: messenger)
        return ret
    }
    
    public func createArgsCodec() -> FlutterMessageCodec & NSObjectProtocol {
          return FlutterStandardMessageCodec.sharedInstance()
    }
    
}
