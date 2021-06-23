package com.fanbook.flutter_text_field.editview;

import android.content.Context;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;

import com.fanbook.flutter_text_field.Utils;
import com.fanbook.flutter_text_field.messages.BlockParams;
import com.fanbook.flutter_text_field.messages.CreationParams;
import com.fanbook.flutter_text_field.messages.ReplaceParams;
import com.fanbook.flutter_text_field.spans.TargetSpan;

import java.util.HashMap;
import java.util.Map;

import io.flutter.plugin.common.BinaryMessenger;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.platform.PlatformView;

import static com.fanbook.flutter_text_field.FlutterTextFieldPlugin.VIEW_TYPE_ID;

public class NativeEditView implements PlatformView, MethodChannel.MethodCallHandler {
    private static final String TAG = "NativeEditView";

    private final Context mContext;
    private final EditText mEditText;
    private MaxHeightScrollView mContainer;
    private MethodChannel methodChannel;
    private double mTextLineHeight;

    private final int DEFAULT_HEIGHT = 40;

    public NativeEditView(Context context, int viewId, Map<String, Object> creationParams, BinaryMessenger
            messenger) {
        mContext = context;
        mEditText = new EditText(context);

//        int resId = context.getResources().getIdentifier("NativeEditTextTheme", "style", mContext.getPackageName());
//        mEditText = new EditText(new ContextThemeWrapper(context, resId));
        // 修改textSelectHandle等样式颜色等，可以直接在app模块的主题中设置相关属性
        // 如果还需要修改图片的话，可以使用上面注释中的方式
        initViewParams(creationParams);
        initMethodChannel(messenger, viewId);
    }

    private void initViewParams(Map<String, Object> params) {
        CreationParams creationParams = new CreationParams(params);
        Log.d(TAG, "initViewParams: " + creationParams);
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT);
        mEditText.setMinLines(1);
        mEditText.setGravity(Gravity.CENTER_VERTICAL);
        mEditText.setLayoutParams(layoutParams);

        double textSize = creationParams.getTextStyle().getFontSize();
        double textHeightRatio = (float) creationParams.getTextStyle().getHeight();

        double verticalPaddingDp = ((DEFAULT_HEIGHT - textSize) / 2);
        int verticalPadding = Utils.dip2px(mContext, (float) (verticalPaddingDp - (textHeightRatio - 1) * textSize)) / 2;
        mEditText.setPadding(Utils.dip2px(mContext, 14), verticalPadding, Utils.dip2px(mContext, 4), verticalPadding);

        mEditText.setWidth(Utils.dip2px(this.mEditText.getContext(), (float) creationParams.getWidth()));
        mEditText.setText(creationParams.getText());
        if (creationParams.getText() != null && creationParams.getText().length() > 0) {
            mEditText.setSelection(creationParams.getText().length());
        }
        mEditText.setTextColor((int) creationParams.getTextStyle().getColor());
        mEditText.setTextSize((float) textSize);

        Utils.setTextLineHeight(mEditText, (float) textHeightRatio);
        mTextLineHeight = Utils.getTextLineHeight(mEditText, (float) textHeightRatio);

        mEditText.setHint(creationParams.getPlaceHolder());
        mEditText.setHintTextColor((int) creationParams.getPlaceHolderStyle().getColor());
        InputFilter[] filters = {new InputFilter.LengthFilter(creationParams.getMaxLength())};
        mEditText.setFilters(filters);
        mEditText.setBackground(null);
        mEditText.setLongClickable(true);

        mContainer = new MaxHeightScrollView(mContext, (int) creationParams.getMaxHeight());
        mContainer.addView(mEditText);
        mContainer.setPadding(0, verticalPadding, 0, verticalPadding);
        mContainer.setHorizontalScrollBarEnabled(false);
        mContainer.setVerticalScrollBarEnabled(false);
    }

    private void initMethodChannel(BinaryMessenger messenger, int viewId) {
        methodChannel = new MethodChannel(messenger, VIEW_TYPE_ID + "_" + viewId);
        methodChannel.setMethodCallHandler(this);
        mEditText.addTextChangedListener(new TextWatcher() {
            int beforeRow = 0;
            int spanLength = -1;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                Log.d(TAG, "beforeTextChanged: " + s.toString() + " start:" + start + ", count:" + count + ", after:" + after);
                beforeRow = mEditText.getLineCount();
                if (start <= 0) return;
                // 获取光标处的TargetSpan
                TargetSpan[] spans = mEditText.getEditableText().getSpans(start + count, start + count, TargetSpan.class);
                if (spans == null || spans.length <= 0) return;
                // count > after 删除，count < after 输入
                // 1.删除时，如果是TargetSpan,需要一次性删除整个span,所以这里记录span的长度，然后在onTextChanged 回调中删除span的字符串
                // 2.输入时，需要考虑光标的问题，如果光标处于span中，需要remove掉这个span的style,使之变成普通文本
                if (count > after) {
                    for (TargetSpan span : spans) {
                        int end = mEditText.getEditableText().getSpanEnd(span);
                        if (end != start + count) continue;
                        String text = span.getText();
                        // 这里长度需要减个1(del键会删除一个,所以onTextChanged回调中需少删一个)
                        spanLength = text.length() - 1;
                        mEditText.getText().removeSpan(span);
                    }
                } else if (count < after && start != mEditText.getText().length()) {
                    for (TargetSpan span : spans) {
                        int end = mEditText.getEditableText().getSpanEnd(span);
                        // 光标在TargetSpan后面时不需要remove掉此span
                        if (end == start) continue;
                        mEditText.getText().removeSpan(span);
                    }
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Log.d(TAG, "onTextChanged: " + s.toString() + " start:" + start + ", count:" + count);
                int changedRow = mEditText.getLineCount();
                if (changedRow != beforeRow) {
                    onTextRowChanged(beforeRow, changedRow);
                }

                // 在删除span时replace会再次触发onTextChanged,　所以删除span的情况不应该走updateValue回调
                if (spanLength > -1) {
                    // 这里删除span
                    int length = spanLength;
                    spanLength = -1;
                    mEditText.getEditableText().replace(start - length, start, "");
                } else {
                    // 这里走updateValue
                    Map<String, Object> params = new HashMap<>();
                    params.put("text", mEditText.getText().toString());
                    params.put("data", getDatas());
                    params.put("selection_start", mEditText.getSelectionStart());
                    params.put("selection_end", mEditText.getSelectionEnd());
                    String inputText = count == 0 ? "" : s.subSequence(start, start + count).toString();
                    params.put("input_text", inputText);
                    methodChannel.invokeMethod("updateValue", params);
                    Log.d(TAG, "onTextChanged: updateValue");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                Log.d(TAG, "afterTextChanged: " + s.toString());
            }

            private void onTextRowChanged(int before, int after) {
                double newHeight = Utils.px2dip(mContext, (float) ((after - 1) * mTextLineHeight)) + DEFAULT_HEIGHT;
                Log.d(TAG, "onTextRowChanged: before " + before + ", after " + after + ", newHeight " + newHeight);
                methodChannel.invokeMethod("updateHeight", newHeight);
            }
        });

        mEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                Log.d(TAG, "onFocusChange: " + hasFocus);
                methodChannel.invokeMethod("updateFocus", hasFocus);
            }
        });
    }

    @Override
    public void onMethodCall(@NonNull MethodCall call, @NonNull MethodChannel.Result result) {
        Log.d(TAG, "onMethodCall: " + call.method);
        switch (call.method) {
            case "setText":
                handleSetText(call, result);
                break;
            case "updateFocus":
                handleUpdateFocus(call, result);
                break;
            case "replace":
                handleReplace(call, result);
                break;
            case "insertText":
                handleInsertText(call, result);
                break;
            case "insertBlock":
                handleInsertBlock(call, result);
                break;
            case "setAlpha":
                handleSetAlpha(call, result);
                break;
            default:
                result.notImplemented();
                break;
        }
    }

    @Override
    public View getView() {
        return mContainer;
    }

    @Override
    public void dispose() {
        methodChannel.setMethodCallHandler(null);
    }

    private void handleSetText(@NonNull MethodCall call, @NonNull MethodChannel.Result result) {
        String content = (String) call.arguments;
        mEditText.setText(content);
        mEditText.setSelection(content.length());
        result.success(null);
    }

    private void handleUpdateFocus(@NonNull MethodCall call, @NonNull MethodChannel.Result result) {
        Boolean focus = (Boolean) call.arguments;
        Log.d(TAG, "handleUpdateFocus: flutter -> android: " + focus);
        if (focus) {
            mEditText.requestFocus();
            Utils.showSoftKeyboard(mContext, mEditText);
        } else {
            mEditText.clearFocus();
            Utils.hideSoftKeyboard(mContext, mEditText);
        }
        result.success(null);
    }

    private void handleReplace(@NonNull MethodCall call, @NonNull MethodChannel.Result result) {
        ReplaceParams replaceParams = new ReplaceParams((Map<String, Object>) call.arguments);
        Log.d(TAG, "handleReplace: " + replaceParams);
        String content = replaceParams.getText();
        int start = replaceParams.getSelectionStart();
        int end = replaceParams.getSelectionEnd();
        if (start < 0 || end < 0 || start > end) {
            result.error(call.method, "replace range error", null);
        } else {
            mEditText.getText().replace(start, end, content);
            result.success(null);
        }
    }

    private void handleInsertBlock(MethodCall call, MethodChannel.Result result) {
        BlockParams blockParams = new BlockParams((Map<String, Object>) call.arguments);
        Log.d(TAG, "handleInsertBlock: " + blockParams);
        final TargetSpan span = new TargetSpan(blockParams.getPrefix(), blockParams.getName(), blockParams.getData());
        SpannableString spannableString = new SpannableString(span.getText());
        // 使用SPAN_EXCLUSIVE_EXCLUSIVE时会导致换行键报错
        spannableString.setSpan(span, 0, spannableString.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        int currentSelectionStart = mEditText.getSelectionStart();
        int backSpaceLength = blockParams.getBackSpaceLength();
        if (currentSelectionStart < backSpaceLength) {
            throw new IllegalArgumentException("Back space length or selection start param error!!!");
        }
        // 插入block时需要将app那边输入的@#去掉，根据这个backspaceLength来判断
        if (blockParams.getBackSpaceLength() > 0) {
            mEditText.getText().replace(currentSelectionStart - backSpaceLength, currentSelectionStart, spannableString);
        } else {
            mEditText.getText().insert(mEditText.getSelectionStart(), spannableString);
        }
        result.success(null);
    }

    private void handleInsertText(MethodCall call, MethodChannel.Result result) {
        String content = (String) call.arguments;
        mEditText.getText().insert(mEditText.getSelectionStart(), content);
        result.success(null);
    }

    private void handleSetAlpha(MethodCall call, MethodChannel.Result result) {
        // TODO iOS有这个功能
        result.success(null);
    }

    private String getDatas() {
        TargetSpan[] spans = mEditText.getEditableText().getSpans(0, mEditText.length(), TargetSpan.class);
        String ret = mEditText.getText().toString();
        Log.d(TAG, "text: " + ret);
        for (TargetSpan span : spans) {
            Log.d(TAG, "getDatas: " + span);
            ret = ret.replace(span.getText(), span.getData());
        }
        Log.d(TAG, "data: " + ret);
        return ret;
    }
}
