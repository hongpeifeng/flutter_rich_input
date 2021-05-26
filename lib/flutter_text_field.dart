// You have generated a new plugin project without
// specifying the `--platforms` flag. A plugin project supports no platforms is generated.
// To add platforms, run `flutter create -t plugin --platforms <platforms> .` under the same
// directory. You can also find a detailed instruction on how to add platforms in the `pubspec.yaml` at https://flutter.dev/docs/development/packages-and-plugins/developing-packages#plugin-platforms.

import 'dart:io';
import 'package:flutter/material.dart';
import 'package:flutter/services.dart';

class YYTextEditingValue {
  final String text;
  final String data;
  final TextRange selection;
  final String inputText;

  const YYTextEditingValue({
    this.text = '',
    this.data = '',
    this.inputText = '',
    this.selection = const TextRange.collapsed(0),
  });

  static const YYTextEditingValue empty = YYTextEditingValue();

  YYTextEditingValue copyWith({
    String text,
    String data,
    String inputText,
    TextRange selection,
  }) {
    return YYTextEditingValue(
      text: text ?? this.text,
      data: data ?? this.data,
      inputText: inputText ?? '',
      selection: selection ?? this.selection,
    );
  }

  static YYTextEditingValue fromJSON(Map encoded) {
    if (encoded == null) return YYTextEditingValue.empty;
    return YYTextEditingValue(
      text: encoded['text'] as String,
      data: encoded['data'] as String,
      selection: TextRange(
        start: encoded['selection_start'] as int,
        end: encoded['selection_end'] as int,
      ),
      inputText: encoded['input_text'] as String,
    );
  }
}

class YYTextFieldController extends ValueNotifier<YYTextEditingValue> {
  MethodChannel _channel;
  TextStyle _defaultRichTextStyle;
  String get text => value.text;
  String get data => value.data;

  YYTextFieldController() : super(YYTextEditingValue.empty) {
    _defaultRichTextStyle =
        TextStyle(color: Colors.lightBlueAccent, fontSize: 14, height: 1.17);
  }

  void setViewId(String viewId) {
    if (_channel != null) return;
    _channel = MethodChannel('com.fanbook.yytextfield_$viewId');
  }

  void setMethodCallHandler(Future<dynamic> Function(MethodCall call) handler) {
    _channel.setMethodCallHandler(handler);
  }

  Future insertAtName(String name,
      {String data = '', TextStyle textStyle}) async {
    insertBlock(name, data: data, textStyle: textStyle, prefix: '@');
  }

  Future insertChannelName(String name,
      {String data = '', TextStyle textStyle}) async {
    insertBlock(name, data: data, textStyle: textStyle, prefix: '#');
  }

  Future insertBlock(String name,
      {String data = '', TextStyle textStyle, String prefix = ''}) {
    textStyle ??= _defaultRichTextStyle;
    return _channel.invokeMethod("insertBlock", {
      'name': name,
      'data': data,
      'prefix': prefix,
      'textStyle': {
        'color': textStyle.color.value,
        'fontSize': textStyle.fontSize,
        'height': textStyle.height ?? 1.17
      }
    });
  }

  Future updateFocus(bool focus) async {
    return _channel.invokeMethod("updateFocus", focus);
  }

  @override
  set value(YYTextEditingValue newValue) {
    super.value = newValue;
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
  final double width;

  const YYTextField({
    @required this.controller,
    @required this.focusNode,
    this.text = '',
    this.textStyle,
    this.placeHolder = '',
    this.placeHolderStyle,
    this.maxLength = 5000,
    this.width,
  });

  @override
  _YYTextFieldState createState() => _YYTextFieldState();
}

class _YYTextFieldState extends State<YYTextField> {
  double _height = 40;

  Map createParams() {
    return {
      'width': widget.width ?? MediaQuery.of(context).size.width,
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

  Future<void> _handlerCall(MethodCall call) async {
    switch (call.method) {
      case 'updateHeight':
        print("flutter height: $call.arguments");
        setState(() {
          _height = call.arguments ?? 40;
        });
        break;
      case 'updateFocus':
        print("updateFocus: ${call.arguments}");
        final focus = call.arguments ?? false;
        if (focus) {
          widget.focusNode.requestFocus();
        } else {
          widget.focusNode.unfocus();
        }
        break;
      case 'updateValue':
        final Map temp = call.arguments;
        final value = YYTextEditingValue.fromJSON(temp);
        widget.controller.value = value;
        break;
      default:
        break;
    }
  }

  @override
  Widget build(BuildContext context) {
    if (Platform.isIOS) {
      return SizedBox(
        height: _height,
        child: Focus(
          focusNode: widget.focusNode,
          onFocusChange: (focus) {
            widget.controller.updateFocus(focus);
          },
          child: UiKitView(
            viewType: "com.fanbook.yytextfield",
            creationParams: createParams(),
            creationParamsCodec: const StandardMessageCodec(),
            onPlatformViewCreated: (viewId) {
              widget.controller.setViewId('$viewId');
              widget.controller.setMethodCallHandler(_handlerCall);
            },
          ),
        ),
      );
    }
    return Text('暂不支持该平台');
  }
}
