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
        this.prefix = prefix;
        this.name = name;
        this.data = data;
        if (!"@".equals(prefix) && !"#".equals(prefix)) {
            throw new IllegalArgumentException("Only prefix '@' or '#' supported!!!");
        }
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
