package com.fanbook.flutter_text_field.spans;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.text.TextUtils;
import android.text.style.TextAppearanceSpan;

public class TargetSpan extends TextAppearanceSpan {
    private final String name;
    private final String prefix;
    private final String data;
    private final String text;

    public static final int NAME_SPAN = 1;
    public static final int EMOJI_SPAN = 2;

    public TargetSpan(int spanType, String prefix, String name, String data) {
        this(prefix, name, data, spanType == NAME_SPAN ? 0xFF3451b1 : 0xFF1F2125);
    }

    public TargetSpan(String prefix, String name, String data, int color) {
        super(null, 0, 0, ColorStateList.valueOf(color), null);
        this.prefix = prefix;
        this.name = name;
        this.data = data;
        this.text = prefix + name;
    }

    public static int spanType(String prefix, String name) {
        if (TextUtils.isEmpty(name)|| TextUtils.isEmpty(name)) return -1;
        if ("@".equals(prefix) || "#".equals(prefix)) return NAME_SPAN;
        if (name.startsWith("[") && name.endsWith("]")) return EMOJI_SPAN;
        return -1;
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
