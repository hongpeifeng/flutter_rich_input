package com.fanbook.flutter_text_field.editview;

import android.content.Context;
import android.graphics.Color;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;

import com.fanbook.flutter_text_field.R;
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
    private MethodChannel methodChannel;

    public NativeEditView(Context context, int viewId, Map<String, Object> creationParams, BinaryMessenger
            messenger) {
        mContext = context;
        mEditText = new EditText(context);
        initViewParams(creationParams);
        initMethodChannel(messenger, viewId);
    }

    private void initViewParams(Map<String, Object> params) {
        CreationParams creationParams = new CreationParams(params);
        Log.d(TAG, "initViewParams: " + creationParams);
        mEditText.setWidth(Utils.dip2px(this.mEditText.getContext(), (float) creationParams.getWidth()));
        mEditText.setText(creationParams.getText());
        mEditText.setTextColor((int) creationParams.getTextStyle().getColor());
        mEditText.setTextSize((float) creationParams.getTextStyle().getFontSize());
        // TODO 文字占高比
        mEditText.setHint(creationParams.getPlaceHolder());
        mEditText.setHintTextColor((int) creationParams.getPlaceHolderStyle().getColor());
        InputFilter[] filters = {new InputFilter.LengthFilter(creationParams.getMaxLength())};
        mEditText.setFilters(filters);
        mEditText.setBackground(null);
    }

    private void initMethodChannel(BinaryMessenger messenger, int viewId) {
        methodChannel = new MethodChannel(messenger, VIEW_TYPE_ID + "_" + viewId);
        methodChannel.setMethodCallHandler(this);
        mEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                Log.d(TAG, "beforeTextChanged: " + s.toString());
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Log.d(TAG, "onTextChanged: " + s.toString());
                Map<String, Object> params = new HashMap<>();
                params.put("text", mEditText.getText().toString());
                params.put("data", "");
                params.put("selection_start", 0);
                params.put("selection_end", 0);
                params.put("input_text", "");
                methodChannel.invokeMethod("updateValue", params);
            }

            @Override
            public void afterTextChanged(Editable s) {
                Log.d(TAG, "afterTextChanged: " + s.toString());
            }
        });

        mEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                Log.d(TAG, "onFocusChange: " + hasFocus);
//                if (hasFocus) {
//                    mEditText.setTextIsSelectable(true);
//                } else {
//                    mEditText.setTextIsSelectable(false);
//                    mEditText.setFocusableInTouchMode(true);
//                    mEditText.setFocusable(true);
//                    mEditText.setClickable(true);
//                    mEditText.setLongClickable(true);
//                }
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
            default:
                result.notImplemented();
                break;
        }
    }

    @Override
    public View getView() {
        return mEditText;
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
    }

    private void handleInsertText(MethodCall call, MethodChannel.Result result) {
        String content = (String) call.arguments;
        mEditText.getText().insert(mEditText.getSelectionStart(), content);
        result.success(null);
    }

}
