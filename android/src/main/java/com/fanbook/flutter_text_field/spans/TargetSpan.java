package com.fanbook.flutter_text_field.spans;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.text.style.TextAppearanceSpan;

public class TargetSpan extends TextAppearanceSpan {
    private final String name;
    private final String prefix;
    private final String data;
    private final String text;

    public TargetSpan(String prefix, String name, String data) {
        this(prefix, name, data, Color.BLUE);
    }

    public TargetSpan(String prefix, String name, String data, int color) {
        super(null, 0, 0, ColorStateList.valueOf(color), null);
        if (!"@".equals(prefix) && !"#".equals(prefix) && !(name.startsWith("[") && name.endsWith("]"))) {
            throw new IllegalArgumentException("Only prefix '@' or '#', or data with '[xxx]' supported!!!");
        }
        this.prefix = prefix;
        this.name = name;
        this.data = data;
        this.text = prefix + name;
    }

    public String getName() {
        return name;
    }

    public String getPrefix() {
        return prefix;
    }

    public String getData() {
        return data;
    }

    public String getText() {
        return text;
    }

    @Override
    public String toString() {
        return "TargetSpan{" +
                "name='" + name + '\'' +
                ", prefix='" + prefix + '\'' +
                ", data='" + data + '\'' +
                ", text='" + text + '\'' +
                '}';
    }
}
