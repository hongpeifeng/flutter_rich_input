package com.fanbook.flutter_text_field.messages;

import java.util.Map;

public class CreationParams {

    private double width;
    private double height;
    private double maxHeight;
    private double minHeight;
    private String text;
    private TextStyle textStyle;
    private String placeHolder;
    private TextStyle placeHolderStyle;
    private int maxLength;
    private boolean done;

    private CreationParams() {
    }

    public CreationParams(Map<String, Object> params) {
        Object width = params.get("width");
        this.width = width == null ? 0 : (double) width;

        Object height = params.get("height");
        this.height = height == null ? 0 : (double) height;

        Object maxHeight = params.get("maxHeight");
        this.maxHeight = maxHeight == null ? 0 : (double) maxHeight;

        Object minHeight = params.get("minHeight");
        this.minHeight = minHeight == null ? 0 : (double) minHeight;

        Object text = params.get("text");
        this.text = text == null ? "" : (String) text;

        Object textStyle = params.get("textStyle");
        if (textStyle != null) {
            Map<String, Object> tsMap = (Map<String, Object>) textStyle;
            this.textStyle = new TextStyle(tsMap);
        } else {
            this.textStyle = new TextStyle();
        }

        Object placeHolder = params.get("placeHolder");
        this.placeHolder = placeHolder == null ? "" : (String) placeHolder;

        Object placeHolderStyle = params.get("placeHolderStyle");
        if (placeHolderStyle != null) {
            Map<String, Object> tsMap = (Map<String, Object>) placeHolderStyle;
            this.placeHolderStyle = new TextStyle(tsMap);
        } else {
            this.placeHolderStyle = new TextStyle();
        }

        Object maxLength = params.get("maxLength");
        this.maxLength = maxLength == null ? 5000 : (int) maxLength;

        Object done = params.get("done");
        this.done = done != null && (boolean) done;
    }

    public double getWidth() {
        return width;
    }

    public String getText() {
        return text;
    }

    public double getMaxHeight() {
        return maxHeight;
    }

    public double getMinHeight() {
        return minHeight;
    }

    public TextStyle getTextStyle() {
        return textStyle;
    }

    public String getPlaceHolder() {
        return placeHolder;
    }

    public TextStyle getPlaceHolderStyle() {
        return placeHolderStyle;
    }

    public int getMaxLength() {
        return maxLength;
    }

    public double getHeight() {
        return height;
    }

    public boolean isDone() {
        return done;
    }

    @Override
    public String toString() {
        return "CreationParams{" +
                "width=" + width +
                ", height=" + height +
                ", maxHeight=" + maxHeight +
                ", minHeight=" + minHeight +
                ", text='" + text + '\'' +
                ", textStyle=" + textStyle +
                ", placeHolder='" + placeHolder + '\'' +
                ", placeHolderStyle=" + placeHolderStyle +
                ", maxLength=" + maxLength +
                ", done=" + done +
                '}';
    }
}
