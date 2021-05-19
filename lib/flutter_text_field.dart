// You have generated a new plugin project without
// specifying the `--platforms` flag. A plugin project supports no platforms is generated.
// To add platforms, run `flutter create -t plugin --platforms <platforms> .` under the same
// directory. You can also find a detailed instruction on how to add platforms in the `pubspec.yaml` at https://flutter.dev/docs/development/packages-and-plugins/developing-packages#plugin-platforms.

import 'dart:io';
import 'package:flutter/material.dart';
import 'package:flutter/services.dart';

class YYTextFieldController {
  MethodChannel _channel;

  void setViewId(String viewId) {
    if (_channel != null) return;
    _channel = MethodChannel('com.fanbook.yytextfield_$viewId');
    _channel.setMethodCallHandler(_handleMethodCall);
  }

  Future _handleMethodCall(MethodCall call) async {
    switch (call.method) {
      case "___":
        break;
    }
  }

  Future insertAtName(String name) async {
    return _channel.invokeMethod("insertAtName", name);
  }

  Future insertChannelName(String name) async {
    return _channel.invokeMethod("insertChannelName", name);
  }
}

class YYTextField extends StatefulWidget {
  final String text;
  final TextStyle textStyle;
  final String placeHolder;
  final TextStyle placeHolderStyle;
  final int maxLength;
  final FocusNode focusNode;
  final YYTextFieldController controller;

  const YYTextField({
    @required this.controller,
    @required this.focusNode,
    this.text = '',
    this.textStyle,
    this.placeHolder = '',
    this.placeHolderStyle,
    this.maxLength = 5000,
  });

  @override
  _YYTextFieldState createState() => _YYTextFieldState();
}

class _YYTextFieldState extends State<YYTextField> {
  Map createParams() {
    return {
      'text': widget.text,
      'textStyle': {
        'color': widget.textStyle.color.value,
        'fontSize': widget.textStyle.fontSize,
        'height': widget.textStyle.height ?? 1.17
      },
      'placeHolder': widget.placeHolder,
      'placeHolderStyle': {
        'color': widget.placeHolderStyle.color.value,
        'fontSize': widget.placeHolderStyle.fontSize,
        'height': widget.placeHolderStyle.height ?? 1.35
      },
      'maxLength': widget.maxLength,
    };
  }

  @override
  Widget build(BuildContext context) {
    if (Platform.isIOS) {
      return Focus(
        focusNode: widget.focusNode,
        onFocusChange: (focus) {},
        child: UiKitView(
          viewType: "com.fanbook.yytextfield",
          creationParams: createParams(),
          creationParamsCodec: const StandardMessageCodec(),
          onPlatformViewCreated: (viewId) {
            widget.controller.setViewId('$viewId');
          },
        ),
      );
    }
    return Text('暂不支持该平台');
  }
}
