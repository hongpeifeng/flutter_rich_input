//
//  YYTextField+Api.swift
//  flutter_text_field
//
//  Created by lionel.hong on 2021/5/25.
//

import Foundation

// MARK: - 主要是处理回调给flutter层的API

extension RichTextField {
    /// 插入富文本的核心函数
    /// - Parameters:
    ///   - name: 显示的内容
    ///   - data: 发送需要替代成的内容
    ///   - textStyle: 文字样式
    ///   - prefix: 前缀
    func insertBlock(name: String, data: String, textStyle: [String: Any]?, prefix: String = "") {
        let inputText = "\(prefix)\(name)"
        editText(inputText: inputText) { _ in

            let atName = inputText

            let str = NSMutableAttributedString(attributedString: textView.attributedText!)

            let location = textView.selectedRange.location

            var attr: [NSAttributedString.Key: Any] = textStyle2Attribute(textStyle: textStyle, defaultAttr: atAttributes)
            attr[bindClassKey] = UUID().uuidString
            attr[dataKey] = data

            str.insert(NSAttributedString(string: atName, attributes: attr), at: location)

            textView.attributedText = str

            textView.selectedRange = NSMakeRange(location + atName.count, 0)
        }
    }

    /// 焦点更新
    /// - Parameter focus: 输入框是否有焦点
    func updateFocus(focus: Bool) {
        channel.invokeMethod("updateFocus", arguments: focus)
    }

    func updateWidth(width: Double) {
        var frame = textView.frame
        frame.size.width = CGFloat(width)
        textView.frame = frame
    }

    /// flutter controller value 回调
    func updateValue() {
        channel.invokeMethod("updateValue", arguments: [
            "text": textView.text ?? "",
            "data": getData(),
            "selection_start": textView.selectedRange.location,
            "selection_end": textView.selectedRange.location + textView.selectedRange.length,
            "input_text": bakReplacementText,
        ])
    }

    /// 返回真实要上传的文字
    /// - Returns: 真实要上传的文字
    func getData() -> String {
        var keys = Set<String>()
        var ret = ""
        textView.attributedText.enumerateAttributes(in: NSRange(location: 0, length: textView.attributedText.string.count), options: NSAttributedString.EnumerationOptions.longestEffectiveRangeNotRequired) { attr, range, _ in
            let keyString = getBindClassValue(attr: attr) ?? ""
            if keyString.count == 0 {
                let startIndex = textView.text.startIndex
                let beginIndex = textView.text.index(startIndex, offsetBy: range.location)
                let endIndex = textView.text.index(startIndex, offsetBy: range.location + range.length)
                let str = String(textView.text[beginIndex..<endIndex])
                ret.append(str)
            } else {
                if !keys.contains(keyString) {
                    keys.insert(keyString)
                    ret.append(getDataValue(attr: attr) ?? "")
                }
            }
        }
        return ret
    }

    /// 设置文字
    /// - Parameter text: 文字内容
    func setText(text: String) {
        let isOriginEmpty = textView.text.isEmpty
        textView.attributedText = NSAttributedString(string: text, attributes: defaultAttributes)
        if isOriginEmpty || text.isEmpty { // 必要时重绘placeHolder，不设置textView不会刷新
            textView.attributedPlaceholder = textView.attributedPlaceholder
        }
        bakReplacementText = text
        // 更新一下数据
        updateValue()
    }

    /// 区域替换文字
    /// - Parameters:
    ///   - text: 替换的文字
    ///   - range: 区域
    func replace(text: String, range: NSRange) {
        if range.location < 0 || range.location + range.length > textView.attributedText.string.count {
            return
        }
        editText(inputText: text, replaceLength: range.length, range: range) { needchange in
            if !needchange {
                return
            }
            let str = NSMutableAttributedString(attributedString: textView.attributedText!)
            str.replaceCharacters(in: range, with: text)
            textView.attributedText = str
            textView.selectedRange = NSRange(location: range.location + text.count, length: 0)
        }
    }

    /// 从光标位置插入指定内容
    /// - Parameter text: 内容
    func insertText(text: String) {
        editText(inputText: text) { _ in
            let str = NSMutableAttributedString(attributedString: textView.attributedText!)

            let location = textView.selectedRange.location

            str.insert(NSAttributedString(string: text, attributes: defaultAttributes), at: location)

            textView.attributedText = str

            textView.selectedRange = NSMakeRange(location + text.count, 0)
        }
    }

    /// 编辑文字前后需要处理的事情，统一处理
    /// - Parameters:
    ///   - inputText: 输入的内容
    ///   - replaceLength: 被替换文字的长度
    ///   - range: 替换的范围
    ///   - block: 处理block
    /// - Returns: void
    func editText(inputText: String, replaceLength: Int = 0, range: NSRange? = nil, _ block: (Bool) -> Void) {
        if textView.maxLength != 0 && (inputText.count - replaceLength + textView.attributedText.string.count > textView.maxLength) {
            return
        }

        let isOriginEmpty = textView.text.isEmpty

        let ret = textView(textView, shouldChangeTextIn: range ?? textView.selectedRange, replacementText: "")

        block(ret)

        if isOriginEmpty { // 必要时重绘placeHolder，不设置textView不会刷新
            textView.attributedPlaceholder = textView.attributedPlaceholder
        }

        bakReplacementText = inputText
        // 更新一下数据
        updateValue()
    }

    /// 将文字回调到flutter层，处理flutter层的onDone和onSubmit等回调
    func submitText() {
        channel.invokeMethod("submitText", arguments: textView.text)
    }
}
