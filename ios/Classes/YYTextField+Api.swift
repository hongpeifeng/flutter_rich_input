//
//  YYTextField+Api.swift
//  flutter_text_field
//
//  Created by lionel.hong on 2021/5/25.
//

import Foundation


extension YYTextField {
    
    func insertAtString(name: String, data: String, textStyle: [String: Any]?) {
        insertBlock(name: name, data: data, textStyle: textStyle, prefix: "@")
    }
    
    func insertChannelString(name: String, data: String, textStyle: [String: Any]?) {
        insertBlock(name: name, data: data, textStyle: textStyle, prefix: "#")
    }

    func insertBlock(name: String, data: String, textStyle: [String: Any]?, prefix:String = "") {
        let inputText = "\(prefix)\(name) "
        editText(inputText: inputText) {
            
            let atName = inputText
            
            let str = NSMutableAttributedString(attributedString: textView.attributedText!)
            
            let location = textView.selectedRange.location
            
            var attr:[NSAttributedString.Key: Any] = textStyle2Attribute(textStyle: textStyle, defaultAttr: atAttributes)
            attr[bindClassKey] = UUID().uuidString
            attr[dataKey] = data
            
            str.insert(NSAttributedString(string: atName,attributes: attr), at: location)
            
            textView.attributedText = str
            
            textView.selectedRange = NSMakeRange(location + atName.count, 0)
            
        }
    }
    
    
    func updateFocus(focus: Bool) {
        channel.invokeMethod("updateFocus", arguments: focus)
    }
    
    func updateValue() {
        channel.invokeMethod("updateValue", arguments: [
            "text": textView.text ?? "",
            "data": getData(),
            "selection_start": textView.selectedRange.location,
            "selection_end": textView.selectedRange.location + textView.selectedRange.length,
            "input_text": bakReplacementText
        ])
    }
    
    func getData() -> String {
        var keys = Set<String>()
        var ret = ""
        textView.attributedText.enumerateAttributes(in: NSRange(location: 0, length: textView.attributedText.string.count), options: NSAttributedString.EnumerationOptions.longestEffectiveRangeNotRequired) { (attr, range, xxx) in
            let keyString = getBindClassValue(attr: attr) ?? ""
            if keyString.count == 0 {
                ret.append(textView.attributedText.attributedSubstring(from: range).string)
            } else {
                if !keys.contains(keyString) {
                    keys.insert(keyString)
                    ret.append(getDataValue(attr: attr) ?? "")
                }
            }
        }
        return ret
    }
    
    func setText(text: String) {
        textView.attributedText = NSAttributedString(string: text, attributes: defaultAttributes)
    }
    
    func replace(text: String, range: NSRange) {
        if range.location + range.length > text.count {
            return
        }
        editText(inputText: text, replaceLength: range.length) {
            let str = NSMutableAttributedString(attributedString: textView.attributedText!)
            str.replaceCharacters(in: range, with: text)
            textView.attributedText = str
            textView.selectedRange = NSRange(location: range.location + text.count, length: 0)
        }
    }
    
    func insertText(text: String) {
        editText(inputText: text) {
            let str = NSMutableAttributedString(attributedString: textView.attributedText!)
            
            let location = textView.selectedRange.location
            
            str.insert(NSAttributedString(string: text,attributes: defaultAttributes), at: location)
            
            textView.attributedText = str
            
            textView.selectedRange = NSMakeRange(location + text.count, 0)
        }
    }
    
    func editText(inputText:String, replaceLength:Int = 0, _ block:()->()) {
        if textView.maxLength != 0 && (inputText.count - replaceLength + textView.attributedText.string.count > textView.maxLength) {
            return
        }
        
        let isOriginEmpty = textView.text.isEmpty
        
        _ = self.textView(textView, shouldChangeTextIn: textView.selectedRange, replacementText: "")
        
        block()
        
        if (isOriginEmpty) { // 必要时重绘placeHolder，不设置textView不会刷新
            textView.attributedPlaceholder = textView.attributedPlaceholder
        }
        
        bakReplacementText = inputText
        // 更新一下数据
        updateValue()
    }
    
}
