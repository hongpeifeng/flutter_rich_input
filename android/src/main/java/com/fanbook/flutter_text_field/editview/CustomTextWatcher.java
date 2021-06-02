package com.fanbook.flutter_text_field.editview;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextWatcher;
import android.text.style.TextAppearanceSpan;
import android.util.Log;
import android.widget.EditText;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.flutter.plugin.common.MethodChannel;

public class CustomTextWatcher implements TextWatcher {
    private EditText editText;
    private MethodChannel methodChannel;
    String beforeTextChanged;

    public CustomTextWatcher(EditText editText, MethodChannel methodChannel) {
        this.editText = editText;
        this.methodChannel = methodChannel;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int before, int after) {
        Log.d(TAG, "beforeTextChanged: " + s.toString() + "   start:" + start + "    before:" + before + "    after:" + after);
        beforeTextChanged = s.toString();
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int after) {
        Log.d(TAG, "onTextChanged: " + s.toString() + "   start:" + start + "    before:" + before + "    after:" + after);
        Map<String, Object> params = new HashMap<>();
        params.put("text", editText.getText().toString());
        params.put("data", "");
        params.put("selection_start", 0);
        params.put("selection_end", 0);
        params.put("input_text", "");
        methodChannel.invokeMethod("updateValue", params);

        //进行删除操作
        if(before == 1 && after == 0){
            String startValue = String.valueOf(beforeTextChanged.charAt(start));
            boolean isDeleteBlock = specialStartChar.equals(startValue) || specialEndChar.equals(startValue);
        }

    }

    @Override
    public void afterTextChanged(Editable s) {
        editText.removeTextChangedListener(this);
        SpannableStringBuilder builder = getSpanBuilder(s.toString());
        editText.getText().replace(0, editText.getText().length(), builder);
        editText.addTextChangedListener(this);
        Log.d(TAG, "afterTextChanged: " + s.toString());
    }


    SpannableStringBuilder getSpanBuilder(String text) {
        SpannableStringBuilder spanBuilder = new SpannableStringBuilder();
        Pattern pattern = Pattern.compile(CustomTextWatcher.blockPattern,Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(text);
        int color1 = Color.argb(255, 208, 43, 26);
        int color2 = Color.argb(255, 4, 0, 100);
        int start = 0;
        while (matcher.find()) {
            int s = matcher.start();
            int e = matcher.end();
            String unMatchGroup = text.substring(start, s);
            String matchGroup = text.substring(s, e);
            SpannableStringBuilder unMatchSpan = new SpannableStringBuilder(unMatchGroup);
            SpannableStringBuilder matchSpan = new SpannableStringBuilder(matchGroup);


            unMatchSpan.setSpan(makeFontColorSpan(color1), 0, unMatchSpan.length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
            matchSpan.setSpan(makeFontColorSpan(color2), 0, matchSpan.length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
            spanBuilder.append(unMatchSpan);
            spanBuilder.append(matchSpan);
            start = e;
        }
        if(start < text.length()) {
            String unMatchGroup = text.substring(start);
            SpannableStringBuilder unMatchSpan = new SpannableStringBuilder(unMatchGroup);
            unMatchSpan.setSpan(makeFontColorSpan(color1), 0, unMatchSpan.length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
            spanBuilder.append(unMatchSpan);
        }
        return spanBuilder;
    }

    private TextAppearanceSpan makeFontColorSpan(int color) {
        return new TextAppearanceSpan(null, 0, 0, ColorStateList.valueOf(color), null);
    }

    private static final String TAG = "CustomTextWatcher";


    static final String blockPattern = "\u200B(.*?)\u200C";
    static final String specialStartChar = "\u200B";
    static final String specialEndChar = "\u200C";

}

class RichBlock {
    String text;
    String data;
    CusTextStyle cusTextStyle;

    public RichBlock(String text, String data, CusTextStyle cusTextStyle) {
        this.text = text;
        this.data = data;
        this.cusTextStyle = cusTextStyle;
    }
}

class CusTextStyle {
    int color;
    float fontSize;

    public CusTextStyle(int color, float fontSize) {
        this.color = color;
        this.fontSize = fontSize;
    }
}

