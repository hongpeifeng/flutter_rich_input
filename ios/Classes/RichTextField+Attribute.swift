//
//  TextField+Range.swift
//  flutter_text_field
//
//  Created by lionel.hong on 2021/5/31.
//

import Foundation

extension RichTextField {
    
    /// flutter TextStyle 转 Swift 富文本属性
    /// - Parameters:
    ///   - textStyle: flutter TextStyle
    ///   - defaultAttr: 默认熟悉
    /// - Returns: Swift 富文本属性
    func textStyle2Attribute(textStyle: [String: Any]?, defaultAttr: [NSAttributedString.Key: Any]?) -> [NSAttributedString.Key: Any] {
        guard let textStyle = textStyle else {
            return defaultAttr ?? [:]
        }
        let textColorValue = (textStyle["color"] as? Int) ?? 0
        let fontSize = (textStyle["fontSize"] as? Int) ?? 14
        let height = (textStyle["height"] as? CGFloat) ?? 1.25
        let textColor = UIColor(color: textColorValue)
        
        let paragraphStyle = NSMutableParagraphStyle()
        paragraphStyle.lineSpacing = height
        
        return [
            bindClassKey: "",
            .font: UIFont.systemFont(ofSize: CGFloat(fontSize)),
            .foregroundColor: textColor,
            .paragraphStyle :paragraphStyle,
        ]
    }
    
    /// 获取该属性下的文字data熟悉
    /// - Parameter attr: 富文本熟悉
    /// - Returns: data
    func getDataValue(attr: [NSAttributedString.Key: Any]) -> String? {
        func isDataClass(attr: [NSAttributedString.Key: Any]) -> Bool {
            return attr.keys.contains(dataKey) && (attr[dataKey] as! String).count > 0
        }

        if !isDataClass(attr: attr) {
            return nil
        }
        return attr[dataKey] as? String
    }
    
    /// 获取绑定属性里面的ID
    /// - Parameter attr: 属性
    /// - Returns: ID
    func getBindClassValue(attr: [NSAttributedString.Key: Any]) -> String? {
        func isBindClass(attr: [NSAttributedString.Key: Any]) -> Bool {
            return attr.keys.contains(bindClassKey) && (attr[bindClassKey] as! String).count > 0
        }

        if !isBindClass(attr: attr) {
            return nil
        }
        return attr[bindClassKey] as? String
    }
    
    /// 获取绑定熟悉里面的ID
    /// - Parameter position: 位置
    /// - Returns: ID
    func getBindClassValue(position: Int) -> String? {
        if position <= 0 { return nil }
        let attr = textView.attributedText.attributes(at: position, effectiveRange: nil)
        return getBindClassValue(attr: attr)
    }
    
    /// 获取当前位置富文本的区间，如果没有富文本就直接返回NSRange()
    /// - Parameter position: 位置
    /// - Returns: 富文本区间
    func getCurrentRange(position: Int) -> NSRange {
        if position < 0 || position >= textView.attributedText.length {
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

        _position = _location + _length
        while _position < textView.text.count {
            var range = NSRange()
            let attr = textView.attributedText.attributes(at: _position, effectiveRange: &range)
            if value != getBindClassValue(attr: attr) {
                break
            } else {
                _length = range.length + range.location - _location
                _position = range.location + range.length + 1
            }
        }

        return NSRange(location: _location, length: _length)
    }
}
