//
//  YYTextField.swift
//  Runner
//
//  Created by lionel.hong on 2021/5/11.
//

import Foundation
import Flutter
import GrowingTextView

let bindClassKey = NSAttributedString.Key(rawValue: "BindClassKey")

class YYTextField : NSObject,FlutterPlatformView,GrowingTextViewDelegate {
    
    var viewId:Int64 = -1
    var textView:GrowingTextView!
    var channel:FlutterMethodChannel!
    
    var defaultAttributes: [NSAttributedString.Key: Any] = [
        bindClassKey: "",
        .font: UIFont.systemFont(ofSize: 14),
        .foregroundColor: UIColor.black,
    ]
    
    var atAttributes: [NSAttributedString.Key: Any] = [
        .font: UIFont.systemFont(ofSize: 14),
        .foregroundColor: UIColor.blue,
    ]
    
    func getAtAttributes() -> [NSAttributedString.Key: Any] {
        return [
            bindClassKey: UUID().uuidString,
            .font: UIFont.systemFont(ofSize: 14),
            .foregroundColor: UIColor.blue,
        ]
    }
    
    init(frame: CGRect, viewId: Int64, args:Any?,  messenger: FlutterBinaryMessenger) {
        // 处理通信
        self.viewId = viewId
        // 页面初始化
        var _frame = frame
        if _frame.size.width == 0 {
            _frame = CGRect(x: frame.origin.x, y: frame.origin.y, width: UIScreen.main.bounds.size.width, height: 40)
        }
    
        let args = args as? [String: Any]
        
        let initText = (args?["text"] as? String) ?? ""
        
        let text:NSMutableAttributedString = NSMutableAttributedString(string: initText)
    
        super.init()
        
        channel = FlutterMethodChannel(name: "com.fanbook.yytextfield_\(viewId)", binaryMessenger: messenger)
        channel.setMethodCallHandler { [weak self] (call, result) in
            self?.handlerMethodCall(call)
        }
        
        textView = GrowingTextView(frame: _frame)
        textView.attributedText = text
        textView.textContainerInset = UIEdgeInsets(top: 10, left: 10, bottom: 10, right: 10)
        textView.delegate = self
        textView.backgroundColor = #colorLiteral(red: 0.501960814, green: 0.501960814, blue: 0.501960814, alpha: 1).withAlphaComponent(0.2)
        textView.maxHeight = 142
        textView.minHeight = 40
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
        
        let str = NSMutableAttributedString(attributedString: textView.attributedText!)
    
        let location = textView.selectedRange.location
        
        let attr:[NSAttributedString.Key: Any] = getAtAttributes()
        
        str.insert(NSAttributedString(string: atName,attributes: attr), at: location)
    
        textView.attributedText = str
        
        textView.selectedRange = NSMakeRange(location + atName.count + 1, 0)
    }
    
    func appendChannelString(name: String) {
        let atName = "#\(name) "
        
        let str = NSMutableAttributedString(attributedString: textView.attributedText!)
    
        let location = textView.selectedRange.location
        
        let attr:[NSAttributedString.Key: Any] = getAtAttributes()
        
        str.insert(NSAttributedString(string: atName,attributes: attr), at: location)
    
        textView.attributedText = str
        
        textView.selectedRange = NSMakeRange(location + atName.count + 1, 0)
    }
    
    func view() -> UIView {
        return textView
    }
    
    
    func textView(_ textView: UITextView, shouldChangeTextIn range: NSRange, replacementText text: String) -> Bool{
        // 重置输入样式
        textView.typingAttributes = defaultAttributes
        // 输入文字
        if (range.location >= textView.attributedText.length) {
            return true
        }
        if range.length > 1 {
            // 需要把包含在range这个区间头部和尾部的富文本样式去掉
            let beginRange = getCurrentRange(position: range.location)
            let count = (textView.text as NSString).substring(with: beginRange).trimmingCharacters(in: .whitespaces).count
            if beginRange != NSRange() && range.location < beginRange.location + count {
                let str = NSMutableAttributedString(attributedString: textView.attributedText!)
                str.addAttributes(defaultAttributes, range: beginRange)
                textView.attributedText = str
                textView.selectedRange = range
            }
            let endRange = getCurrentRange(position: range.location + range.length)
            if endRange != NSRange() && beginRange != endRange {
                if range.location + range.length > endRange.location {
                    let str = NSMutableAttributedString(attributedString: textView.attributedText!)
                    str.addAttributes(defaultAttributes, range: endRange)
                    textView.attributedText = str
                    textView.selectedRange = range
                }
            }
            return true
        }
        
        // 删除或者替换文字
        let _range = getCurrentRange(position: range.location)
        if _range != NSRange() {
            let count = (textView.text as NSString).substring(with: _range).trimmingCharacters(in: .whitespaces).count
            // 尾部删除
            if (range.length == 1 && text.count == 0)
                && (range.location + 1 == _range.location + count || range.location + 1 == _range.location + count + 1) {
                let textRange = textView.textRange(from: textView.position(from: textView.beginningOfDocument, offset: _range.location)!, to: textView.position(from: textView.beginningOfDocument, offset: _range.location + _range.length)!)
                textView.replace(textRange!, withText: text)
                return false
            }
            // 尾部添加
            else if (range.length == 0 && text.count > 0)
                && (range.location == _range.location + count || range.location == _range.location + count + 1) {
                return true
            }
            // 正常删除，并去掉样式
            else if range.location > _range.location {
                let str = NSMutableAttributedString(attributedString: textView.attributedText!)
                str.addAttributes(defaultAttributes, range: _range)
                textView.attributedText = str
                textView.selectedRange = range
                return true
            }
            
        }
        
        return true
    }
    
    /// 高度变化回调
    func textViewDidChangeHeight(_ textView: GrowingTextView, height: CGFloat) {
        let frame = textView.frame
        channel.invokeMethod("updateHeight", arguments: height)
        DispatchQueue.main.asyncAfter(deadline: DispatchTime.now() + 0.3) {
            textView.frame = CGRect(x: frame.origin.x, y: frame.origin.y, width: frame.size.width, height: height)
        }
    }

}


// MARK: - Range 处理
extension YYTextField {
    
    func getBindClassValue(attr: [NSAttributedString.Key : Any]) -> String? {
        
        func isBindClass(attr: [NSAttributedString.Key : Any]) -> Bool {
            return attr.keys.contains(bindClassKey) && (attr[bindClassKey] as! String).count > 0
        }
        
        if !isBindClass(attr: attr) {
            return nil
        }
        return attr[bindClassKey] as? String
    }
    
    func getBindClassValue(position: Int) -> String? {
        if position <= 0 { return nil }
        let attr = textView.attributedText.attributes(at: position, effectiveRange: nil)
        return getBindClassValue(attr: attr)
    }
    
    func getCurrentRange(position: Int) -> NSRange {
        if position < 0 || position >= textView.text.count {
            return NSRange()
        }
        
        guard let value = getBindClassValue(position: position) else {
            return NSRange()
        }
        
        var _position = position, _location = 0, _length = 0
        while _position >= 0 {
            var range = NSRange()
            let attr = textView.attributedText.attributes(at: _position, effectiveRange: &range)
            if value != getBindClassValue(attr: attr) {
                break
            } else {
                _location = range.location
                _length += range.length
                _position = _location - 1
            }
        }
        return NSRange(location: _location, length: _length)
    }
    
}
