//
//  UIColor+Hex.swift
//  flutter_text_field
//
//  Created by lionel.hong on 2021/5/24.
//

import Foundation

extension UIColor {
    
    // Hex Int -> UIColor
    convenience init(color: Int) {
        let mask = 0x000000FF
        let a = Int(color >> 24) & mask
        let r = Int(color >> 16) & mask
        let g = Int(color >> 8) & mask
        let b = Int(color) & mask
         
        let red   = CGFloat(r) / 255.0
        let green = CGFloat(g) / 255.0
        let blue  = CGFloat(b) / 255.0
        let alpha  = CGFloat(a) / 255.0
         
        self.init(red: red, green: green, blue: blue, alpha: alpha)
    }
    
}
