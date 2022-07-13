import 'package:flutter/material.dart';

class SharePage extends StatefulWidget {
  SharePage({Key key}) : super(key: key);

  @override
  _SharePageState createState() => _SharePageState();
}

class _SharePageState extends State<SharePage> {
  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: Colors.black,
      body: Center(
        child: Container( height: 200, width: 200, color: Colors.white,
          child: Text('Share!'),
        ),
      ),
    );
  }
}
