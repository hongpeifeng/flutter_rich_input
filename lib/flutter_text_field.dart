// You have generated a new plugin project without
// specifying the `--platforms` flag. A plugin project supports no platforms is generated.
// To add platforms, run `flutter create -t plugin --platforms <platforms> .` under the same
// directory. You can also find a detailed instruction on how to add platforms in the `pubspec.yaml` at https://flutter.dev/docs/development/packages-and-plugins/developing-packages#plugin-platforms.

import 'dart:io';

import 'package:flutter/foundation.dart';
import 'package:flutter/gestures.dart';
import 'package:flutter/material.dart';
import 'package:flutter/rendering.dart';
import 'package:flutter/services.dart';

class RichTextEditingValue {
  final String text;
  final String data;
  final TextRange selection;
  final String inputText;

  const RichTextEditingValue({
    this.text = '',
    this.data = '',
    this.inputText = '',
    this.selection = const TextRange.collapsed(0),
  });

  static const RichTextEditingValue empty = RichTextEditingValue();

  RichTextEditingValue copyWith({
    String text,
    String data,
    String inputText,
    TextRange selection,
  }) {
    return RichTextEditingValue(
      text: text ?? this.text,
      data: data ?? this.data,
      inputText: inputText ?? '',
      selection: selection ?? this.selection,
    );
  }

  static RichTextEditingValue fromJSON(Map encoded) {
    if (encoded == null) return RichTextEditingValue.empty;
    return RichTextEditingValue(
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

class RichTextFieldController extends ValueNotifier<RichTextEditingValue> {
  MethodChannel _channel;
  TextStyle _defaultRichTextStyle;

  String get text => value.text;

  set text(String newText) {
    setText(newText);
    value = value.copyWith(
      text: newText,
      selection: const TextSelection.collapsed(offset: -1),
    );
  }

  String get data => value.data;

  RichTextFieldController({TextStyle defaultRichTextStyle})
      : super(RichTextEditingValue.empty) {
    _defaultRichTextStyle = defaultRichTextStyle ??
        TextStyle(color: Colors.lightBlueAccent, fontSize: 14, height: 1.17);
  }

  Future wait(Function func) async {
    for (int i = 0; i < 5; i++) {
      if (_channel != null) {
        return func?.call();
      }
      await Future.delayed(const Duration(milliseconds: 100));
    }
    return Future.value();
  }

  void setViewId(String viewId) {
    if (_channel != null) return;
    _channel = MethodChannel('com.fanbook.rich_textfield_$viewId');
  }

  void setMethodCallHandler(Future<dynamic> Function(MethodCall call) handler) {
    _channel.setMethodCallHandler(handler);
  }

  Future insertText(String text) async {
    return wait(() => _channel.invokeMethod("insertText", text));
  }

  Future updateWidth(double width) async {
    return wait(() => _channel.invokeMethod("updateWidth", width));
  }

  Future insertAtName(String name,
      {String data = '', TextStyle textStyle, int backSpaceLength = 0}) async {
    return wait(() => insertBlock('$name ',
        data: data,
        textStyle: textStyle,
        prefix: '@',
        backSpaceLength: backSpaceLength));
  }

  Future insertChannelName(String name,
      {String data = '', TextStyle textStyle, int backSpaceLength = 0}) async {
    return wait(() => insertBlock('$name ',
        data: data,
        textStyle: textStyle,
        prefix: '#',
        backSpaceLength: backSpaceLength));
  }

  Future insertBlock(String name,
      {String data = '',
      TextStyle textStyle,
      String prefix = '',
      int backSpaceLength = 0}) {
    textStyle ??= _defaultRichTextStyle;
    return wait(() => _channel.invokeMethod("insertBlock", {
          'name': name,
          'data': data,
          'prefix': prefix,
          'backSpaceLength': backSpaceLength,
          'textStyle': {
            'color': textStyle.color.value,
            'fontSize': textStyle.fontSize,
            'height': textStyle.height ?? 1.17
          }
        }));
  }

  Future updateFocus(bool focus) async {
    return wait(() => _channel.invokeMethod("updateFocus", focus));
  }

  Future replace(String text, TextRange range) async {
    return wait(() => _channel.invokeMethod("replace", {
          'text': text,
          'selection_start': range.start,
          'selection_end': range.end,
        }));
  }

  Future setAlpha(double alpha) async {
    return wait(() => _channel.invokeMethod("setAlpha", alpha));
  }

  Future replaceAll(String text) async {
    return setText(text);
  }

  Future clear() async {
    return setText('');
  }

  Future setText(String text) async {
    return wait(() => _channel.invokeMethod("setText", text));
  }

  @override
  set value(RichTextEditingValue newValue) {
    super.value = newValue;
  }
}

class RichTextField extends StatefulWidget {
  final RichTextFieldController controller;
  final FocusNode focusNode;
  final String text;
  final TextStyle textStyle;
  final String placeHolder;
  final TextStyle placeHolderStyle;
  final int maxLength;
  final double width;
  final double height;
  final double maxHeight;
  final double minHeight;
  final VoidCallback onEditingComplete;
  final Function(String) onSubmitted;
  final Function(String) onChanged;
  final bool autoFocus;
  final bool needEagerGesture;

  const RichTextField({
    @required this.controller,
    @required this.focusNode,
    this.text = '',
    this.textStyle,
    this.placeHolder = '',
    this.placeHolderStyle,
    this.maxLength = 5000,
    this.width,
    this.height,
    this.maxHeight = 142,
    this.minHeight = 32,
    this.onEditingComplete,
    this.onSubmitted,
    this.onChanged,
    this.autoFocus = false,
    this.needEagerGesture = true,
  });

  @override
  _RichTextFieldState createState() => _RichTextFieldState();
}

class _RichTextFieldState extends State<RichTextField> {
  double _height = 40;

  Map createParams() {
    return {
      'width': widget.width ?? MediaQuery.of(context).size.width,
      'height': widget.height,
      'maxHeight': widget.maxHeight,
      'minHeight': widget.minHeight,
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
      'done': widget.onEditingComplete != null || widget.onSubmitted != null
    };
  }

  Future<void> _handlerCall(MethodCall call) async {
    switch (call.method) {
      case 'updateHeight':
        setState(() {
          _height = call.arguments ?? 40;
        });
        break;
      case 'updateFocus':
        final focus = call.arguments ?? false;
        print('////// focus: $focus');
        if (focus) {
          widget.focusNode.requestFocus();
        } else {
          widget.focusNode.unfocus();
        }
        break;
      case 'updateValue':
        final Map temp = call.arguments;
        final value = RichTextEditingValue.fromJSON(temp);
        widget.controller.value = value;
        widget.onChanged?.call(value.text);
        break;
      case 'submitText':
        final text = call.arguments ?? '';
        widget.onSubmitted?.call(text);
        widget.onEditingComplete?.call();
        break;
      default:
        break;
    }
  }

  @override
  void initState() {
    if (widget.autoFocus) widget.controller.updateFocus(true);
    if (widget.height != null) _height = widget.height;
    super.initState();
  }

  @override
  void dispose() {
    widget.controller.setMethodCallHandler(null);
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    final gestureRecognizers = widget.needEagerGesture
        ? <Factory<OneSequenceGestureRecognizer>>[
            new Factory<OneSequenceGestureRecognizer>(
              () => new EagerGestureRecognizer(),
            ),
          ].toSet()
        : null;
    if (Platform.isIOS) {
      return SizedBox(
        height: _height,
        child: Focus(
          focusNode: widget.focusNode,
          onFocusChange: (focus) {
            widget.controller.updateFocus(focus);
          },
          child: UiKitView(
            viewType: "com.fanbook.rich_textfield",
            creationParams: createParams(),
            creationParamsCodec: const StandardMessageCodec(),
            onPlatformViewCreated: (viewId) {
              widget.controller.setViewId('$viewId');
              widget.controller.setMethodCallHandler(_handlerCall);
            },
            gestureRecognizers: gestureRecognizers,
          ),
        ),
      );
    } else if (Platform.isAndroid) {
      return SizedBox(
        height: _height,
        child: Focus(
          focusNode: widget.focusNode,
          onFocusChange: (focus) {
            widget.controller.updateFocus(focus);
          },
          child: PlatformViewLink(
            viewType: "com.fanbook.rich_textfield",
            surfaceFactory: (context, controller) {
              return AndroidViewSurface(
                controller: controller,
                gestureRecognizers: gestureRecognizers,
                hitTestBehavior: PlatformViewHitTestBehavior.opaque,
              );
            },
            onCreatePlatformView: (params) {
              params.onPlatformViewCreated(params.id);
              return PlatformViewsService.initSurfaceAndroidView(
                id: params.id,
                viewType: "com.fanbook.rich_textfield",
                layoutDirection: TextDirection.ltr,
                creationParams: createParams(),
                creationParamsCodec: const StandardMessageCodec(),
              )
                ..addOnPlatformViewCreatedListener((id) {
                  widget.controller.setViewId('$id');
                  widget.controller.setMethodCallHandler(_handlerCall);
                })
                ..create();
            },
          ),
        ),
      );
    } else {
      return Text('?????????????????????');
    }
  }
}
