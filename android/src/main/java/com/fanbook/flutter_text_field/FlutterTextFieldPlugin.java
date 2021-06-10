package com.fanbook.flutter_text_field;

import androidx.annotation.NonNull;

import com.fanbook.flutter_text_field.editview.NativeEditViewFactory;

import io.flutter.embedding.engine.plugins.FlutterPlugin;

/** FlutterTextFieldPlugin */
public class FlutterTextFieldPlugin implements FlutterPlugin {
  /// The MethodChannel that will the communication between Flutter and native Android
  ///
  /// This local reference serves to register the plugin with the Flutter Engine and unregister it
  /// when the Flutter Engine is detached from the Activity
//  private MethodChannel channel;

  public static final String VIEW_TYPE_ID = "com.fanbook.rich_textfield";

  @Override
  public void onAttachedToEngine(@NonNull FlutterPluginBinding flutterPluginBinding) {
//    channel = new MethodChannel(flutterPluginBinding.getBinaryMessenger(), "flutter_text_field");
//    channel.setMethodCallHandler(this);
    flutterPluginBinding
            .getPlatformViewRegistry()
            .registerViewFactory(VIEW_TYPE_ID, new NativeEditViewFactory(flutterPluginBinding.getBinaryMessenger()));
  }

//  @Override
//  public void onMethodCall(@NonNull MethodCall call, @NonNull Result result) {
//    if (call.method.equals("getPlatformVersion")) {
//      result.success("Android " + android.os.Build.VERSION.RELEASE);
//    } else {
//      result.notImplemented();
//    }
//  }

  @Override
  public void onDetachedFromEngine(@NonNull FlutterPluginBinding binding) {
//    channel.setMethodCallHandler(null);
  }
}
