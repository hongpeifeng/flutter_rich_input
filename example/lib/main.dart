import 'dart:math';

import 'package:flutter/material.dart';
import 'package:flutter_text_field/flutter_text_field.dart';

void main() {
  runApp(MyApp());
}

class MyApp extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'Flutter Demo',
      theme: ThemeData(
        primarySwatch: Colors.blue,
      ),
      home: MyHomePage(title: 'Flutter Demo Home Page'),
    );
  }
}

class MyHomePage extends StatefulWidget {
  MyHomePage({Key key, this.title}) : super(key: key);

  final String title;

  @override
  _MyHomePageState createState() => _MyHomePageState();
}

class _MyHomePageState extends State<MyHomePage> {
  int _viewId = 0;
  RichTextFieldController _controller = RichTextFieldController();
  FocusNode _focusNode = FocusNode();

  @override
  void initState() {
    _controller.addListener(() {
      print('value: ${_controller.value.text} / data: ${_controller.data}');
    });
    super.initState();
  }

  @override
  Widget build(BuildContext context) {
    // This method is rerun every time setState is called, for instance as done
    // by the _incrementCounter method above.
    //
    // The Flutter framework has been optimized to make rerunning build methods
    // fast, so that you can just rebuild anything that needs updating rather
    // than having to individually change instances of widgets.
    return Scaffold(
      appBar: AppBar(
        // Here we take the value from the MyHomePage object that was created by
        // the App.build method, and use it to set our appbar title.
        title: Text(widget.title),
      ),
      body: Column(
        children: [
          GestureDetector(
            onHorizontalDragUpdate: (_) {},
            onHorizontalDragCancel: () {},
            onHorizontalDragDown: (_) {},
            onHorizontalDragEnd: (_) {},
            onHorizontalDragStart: (_) {},
            onVerticalDragCancel: () {},
            onVerticalDragDown: (_) {},
            onVerticalDragEnd: (_) {},
            onVerticalDragStart: (_) {},
            onVerticalDragUpdate: (_) {},
            child: SizedBox(
              width: 400,
              child: RichTextField(
                controller: _controller,
                focusNode: _focusNode,
                text: '1233',
//              autoFocus: true,
                textStyle: Theme.of(context)
                    .textTheme
                    .bodyText2
                    .copyWith(color: Colors.black, fontSize: 14),
                placeHolder: '请输入....',
                placeHolderStyle: Theme.of(context)
                    .textTheme
                    .bodyText1
                    .copyWith(color: Colors.redAccent, fontSize: 18),
                maxLength: 5000,
                onChanged: (str) {
                  print('onChanged: $str');
                },
                onSubmitted: (str) {
                  print('onSubmitted: $str');
                },
                onEditingComplete: () {
                  print('onEditingComplete');
                },
              ),
            ),
          ),
          TextButton(
              onPressed: () {
                _controller.insertAtName("周大蝠😀", data: "@{dfdsfsdfsdgdf}");
              },
              child: Text('插入@')),
          TextButton(
              onPressed: () {
                _controller.insertChannelName('蝠道👀😝', data: "#{fdsgdfgdfgdass}");
              },
              child: Text('插入#')),
          TextButton(
              onPressed: () async {
                _controller.replace('123', TextRange(start: 2, end: 3));
              },
              child: Text('replace')),
          TextButton(
              onPressed: () async {
                _controller.setText('dasdasadsafds');
              },
              child: Text('setText')),
          TextButton(
              onPressed: () async {
                _controller.clear();
              },
              child: Text('clear')),
          TextButton(
              onPressed: () async {
                _controller.insertText('abc');
              },
              child: Text('insertText')),
          TextButton(
              onPressed: () async {
                _controller.replace(
                    '',
                    TextRange(
                        start: _controller.text.length - 1,
                        end: _controller.text.length));
              },
              child: Text('delete 最后一个字符')),
          TextButton(
              onPressed: () async {
                var rng = new Random();
                _controller.setAlpha(rng.nextDouble());
              },
              child: Text('设置透明度')),
        ],
      ),
    );
  }
}
