package com.fanbook.flutter_text_field.messages;

import java.util.Map;

public class BlockParams {
    private String name;
    private String data;
    private String prefix;
    private int backSpaceLength;
    private TextStyle textStyle;

    public BlockParams(Map<String, Object> params) {
        Object name = params.get("name");
        this.name = name == null ? "": (String) name;

        Object data = params.get("data");
        this.data = data == null ? "" : (String) data;

        Object prefix = params.get("prefix");
        this.prefix = prefix == null ? "" : (String) prefix;

        Object backSpaceLength = params.get("backSpaceLength");
        this.backSpaceLength = backSpaceLength == null ? 0 : (int) backSpaceLength;

        Object textStyle = params.get("textStyle");
        if (textStyle != null) {
            Map<String, Object> tsMap = (Map<String, Object>) textStyle;
            this.textStyle = new TextStyle(tsMap);
        } else {
            this.textStyle = new TextStyle();
        }
    }

    public String getName() {
        return name;
    }

    public String getData() {
        return data;
    }

    public String getPrefix() {
        return prefix;
    }

    public int getBackSpaceLength() {
        return backSpaceLength;
    }

    public TextStyle getTextStyle() {
        return textStyle;
    }

    @Override
    public String toString() {
        return "BlockParams{" +
                "name='" + name + '\'' +
                ", data='" + data + '\'' +
                ", prefix='" + prefix + '\'' +
                ", backSpaceLength=" + backSpaceLength +
                ", textStyle=" + textStyle +
                '}';
    }
}
