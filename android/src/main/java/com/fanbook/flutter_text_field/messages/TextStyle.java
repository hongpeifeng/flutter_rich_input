package com.fanbook.flutter_text_field.messages;

import androidx.annotation.NonNull;

import java.util.Map;

public class TextStyle {
    private final long color;
    private final double fontSize;
    private final double height;

    TextStyle() {
        this.color = 0x000000;
        this.fontSize = 16;
        this.height = 1.17;
    }

    TextStyle(@NonNull Map<String, Object> params) {
        Object color = params.get("color");
        this.color = color == null ? 0x000000 : (long) color;

        Object fontSize = params.get("fontSize");
        this.fontSize = fontSize == null ? 16 : (double) fontSize;

        Object height = params.get("height");
        this.height = height == null ? 1.17 : (double) height;
    }

    public long getColor() {
        return color;
    }

    public double getFontSize() {
        return fontSize;
    }

    public double getHeight() {
        return height;
    }

    @Override
    public String toString() {
        return "TextStyle{" +
                "color=" + color +
                ", fontSize=" + fontSize +
                ", height=" + height +
                '}';
    }
}