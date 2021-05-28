package com.fanbook.flutter_text_field.editview;

import androidx.annotation.NonNull;

import java.util.Map;

public class CreationParams {

    private double width;
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

    public boolean isDone() {
        return done;
    }

    public static class TextStyle {
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

    private static class PlaceHolderStyle extends TextStyle {
    }

    @Override
    public String toString() {
        return "CreationParams{" +
                "width=" + width +
                ", text='" + text + '\'' +
                ", textStyle=" + textStyle +
                ", placeHolder='" + placeHolder + '\'' +
                ", placeHolderStyle=" + placeHolderStyle +
                ", maxLength=" + maxLength +
                ", done=" + done +
                '}';
    }
}
