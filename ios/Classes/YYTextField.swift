//
//  YYTextField.swift
//  Runner
//
//  Created by lionel.hong on 2021/5/11.
//

import Foundation
import Flutter
import YYText


class YYTextField : NSObject,FlutterPlatformView {
    
    var viewId:Int64 = -1
    var textView:YYTextView!
    var channel:FlutterMethodChannel!
    var defaultAttributes: [NSAttributedString.Key: Any] = [
        .font: UIFont.systemFont(ofSize: 14),
        .foregroundColor: UIColor.black,
    ]
    
    init(frame: CGRect, viewId: Int64, args:Any?,  messenger: FlutterBinaryMessenger) {
        // 处理通信
        self.viewId = viewId
        // 页面初始化
        var _frame = frame
        if _frame.size.width == 0 {
            _frame = CGRect(x: frame.origin.x, y: frame.origin.y, width: UIScreen.main.bounds.size.width, height: UIScreen.main.bounds.size.height - 64)
        }
    
        guard let args = args as? [String: Any] else { return }
        
        
        let text:NSMutableAttributedString = NSMutableAttributedString(string: args["text"])
        text.yy_lineSpacing = 5
        textView = YYTextView(frame: _frame)
        textView.attributedText = text
        textView.textContainerInset = UIEdgeInsets(top: 10 + 64, left: 10, bottom: 10, right: 10)
        textView.allowsCopyAttributedString = true
        textView.allowsPasteAttributedString = true
        
        super.init()
        
        channel = FlutterMethodChannel(name: "com.fanbook.yytextfield_\(viewId)", binaryMessenger: messenger)
        channel.setMethodCallHandler { [weak self] (call, result) in
            self?.handlerMethodCall(call)
        }
        
    }
    
    func handlerMethodCall(_ call: FlutterMethodCall) {
        switch call.method {
        case "insertAtName":
            if let name = call.arguments as? String {
                appendAtString(name: name)
            }
            break
        case "insertChannelName":
            if let name = call.arguments as? String {
                appendChannelString(name: name)
            }
            break
        default:
            break
        }
    }
    
    
    func appendAtString(name: String) {
        let atName = "@\(name) "
        let location = textView.text.count
        let str = NSMutableAttributedString(attributedString: textView.attributedText!)
    
        str.append(NSAttributedString(string: atName,attributes: defaultAttributes))
        
        let bindlingRange = NSMakeRange(location, atName.count)
        let binding = YYTextBinding(deleteConfirm: true)
        str.yy_setTextBinding(binding, range: bindlingRange)
        str.yy_setColor(#colorLiteral(red: 0.5725490451, green: 0, blue: 0.2313725501, alpha: 1), range: bindlingRange)
        
        str.append(NSAttributedString(string: "\u{200B}", attributes: defaultAttributes))
        
        textView.attributedText = str
        textView.selectedRange = NSMakeRange(str.string.count, 0)
    }
    
    func appendChannelString(name: String) {
        let atName = "#\(name) "
        let location = textView.text.count
        let str = NSMutableAttributedString(attributedString: textView.attributedText!)
        str.append(NSAttributedString(string: atName))
        
        let bindlingRange = NSMakeRange(location, atName.count)
        let binding = YYTextBinding(deleteConfirm: true)
        str.yy_setTextBinding(binding, range: bindlingRange)
        str.yy_setColor(#colorLiteral(red: 0.5725490451, green: 0, blue: 0.2313725501, alpha: 1), range: bindlingRange)
        
        textView.attributedText = str
        textView.selectedRange = NSMakeRange(str.string.count, 0)
    }
    
    func view() -> UIView {
        return textView
    }

}

