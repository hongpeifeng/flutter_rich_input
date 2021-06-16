package com.fanbook.flutter_text_field.messages;

import java.util.Map;

public class ReplaceParams {
    private final String text;
    private final int selectionStart;
    private final int selectionEnd;

    public ReplaceParams(Map<String, Object> params) {
        Object text = params.get("text");
        this.text = text == null ? "": (String) text;

        Object start = params.get("selection_start");
        this.selectionStart = start == null ? 0 : (int) start;

        Object end = params.get("selection_end");
        this.selectionEnd = end == null ? 0 : (int) end;
    }

    public String getText() {
        return text;
    }

    public int getSelectionStart() {
        return selectionStart;
    }

    public int getSelectionEnd() {
        return selectionEnd;
    }

    @Override
    public String toString() {
        return "ReplaceParams{" +
                "text='" + text + '\'' +
                ", selectionStart=" + selectionStart +
                ", selectionEnd=" + selectionEnd +
                '}';
    }
}
