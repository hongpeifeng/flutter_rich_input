package com.fanbook.flutter_text_field.editview;

import android.content.Context;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;

import com.fanbook.flutter_text_field.Utils;

import java.util.HashMap;
import java.util.Map;

import io.flutter.plugin.common.BinaryMessenger;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.platform.PlatformView;

import static com.fanbook.flutter_text_field.FlutterTextFieldPlugin.VIEW_TYPE_ID;

public class NativeEditView implements PlatformView, MethodChannel.MethodCallHandler {
    private static final String TAG = "NativeEditView";

    private Context mContext;
    private final EditText mEditText;
    private final MaxHeightScrollView mContainer;
    private MethodChannel methodChannel;
    private double mTextLineHeight;

    private final int DEFAULT_HEIGHT = 40;

    public NativeEditView(Context context, int viewId, Map<String, Object> creationParams, BinaryMessenger
            messenger) {
        mContext = context;

        mEditText = new EditText(context);
        mContainer = new MaxHeightScrollView(context);
        mContainer.addView(mEditText);
        mContainer.setHorizontalScrollBarEnabled(false);
        mContainer.setVerticalScrollBarEnabled(false);

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
        mEditText.setPadding(Utils.dip2px(mContext, 12), verticalPadding, Utils.dip2px(mContext, 4), verticalPadding);
        mContainer.setPadding(0, verticalPadding, 0, verticalPadding);

        mEditText.setWidth(Utils.dip2px(this.mEditText.getContext(), (float) creationParams.getWidth()));
        mEditText.setText(creationParams.getText());
        mEditText.setTextColor((int) creationParams.getTextStyle().getColor());
        mEditText.setTextSize((float) textSize);

        Utils.setTextLineHeight(mEditText, (float) textHeightRatio);
        mTextLineHeight = Utils.getTextLineHeight(mEditText, (float) textHeightRatio) + 10;

        mEditText.setHint(creationParams.getPlaceHolder());
        mEditText.setHintTextColor((int) creationParams.getPlaceHolderStyle().getColor());
        InputFilter[] filters = {new InputFilter.LengthFilter(creationParams.getMaxLength())};
        mEditText.setFilters(filters);
        mEditText.setBackground(null);
        mEditText.setLongClickable(true);
    }

    private void initMethodChannel(BinaryMessenger messenger, int viewId) {
        methodChannel = new MethodChannel(messenger, VIEW_TYPE_ID + "_" + viewId);
        methodChannel.setMethodCallHandler(this);
        mEditText.addTextChangedListener(new TextWatcher() {
            int beforeRow = 0;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                Log.d(TAG, "beforeTextChanged: " + s.toString());
                beforeRow = mEditText.getLineCount();
                mEditText.setNestedScrollingEnabled(false);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Log.d(TAG, "onTextChanged: " + s.toString());
                int changedRow = mEditText.getLineCount();
                if (changedRow != beforeRow) {
                    onTextRowChanged(beforeRow, changedRow);
                }

                Map<String, Object> params = new HashMap<>();
                params.put("text", mEditText.getText().toString());
                // TODO 加block的时候这个data需要修改
                params.put("data", mEditText.getText().toString());
                params.put("selection_start", start);
                params.put("selection_end", start + count);
                params.put("input_text", s.subSequence(start, start + count).toString());
                methodChannel.invokeMethod("updateValue", params);
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
        result.success(null);
    }

    private void handleUpdateFocus(@NonNull MethodCall call, @NonNull MethodChannel.Result result) {
        Boolean focus = (Boolean) call.arguments;
        Log.d(TAG, "handleUpdateFocus: flutter -> android: " + focus);
        if (focus) {
            mEditText.requestFocus();
        } else {
            mEditText.clearFocus();
            Utils.hideSoftKeyboard(mContext, mEditText);
        }
        result.success(null);
    }

    private void handleReplace(@NonNull MethodCall call, @NonNull MethodChannel.Result result) {
        Map<String, Object> args = (Map<String, Object>) call.arguments;
        String content = (String) args.get("text");
        int start = (int) args.get("selection_start");
        int end = (int) args.get("selection_end");
        if (start < 0 || end < 0 || start > end) {
            result.error(call.method, "replace range error", null);
        } else {
            mEditText.getText().replace(start, end, content);
        }
        result.success(null);
    }

    private void handleInsertBlock(MethodCall call, MethodChannel.Result result) {
        result.success(null);
    }

    private void handleInsertText(MethodCall call, MethodChannel.Result result) {
        String content = (String) call.arguments;
        mEditText.getText().insert(mEditText.getSelectionStart(), content);
        result.success(null);
    }

    private void handleSetAlpha(MethodCall call, MethodChannel.Result result) {
        result.success(null);
    }
}
