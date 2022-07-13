import 'dart:ui';
import 'package:auto_size_text/auto_size_text.dart';
import 'package:flutter/material.dart';
import 'package:google_fonts/google_fonts.dart';
import 'package:penchant/model/media.dart';
import 'package:penchant/model/user.dart';
import 'avatar.dart';

class ChannelPlayingNow extends StatelessWidget {
  const ChannelPlayingNow({Key key, @required this.media}) : super(key: key);
  final Media media;

  @override
  Widget build(BuildContext context) {
    if (media == null) {
      return Padding(
        padding: const EdgeInsets.only(left: 50),
        child: Text('So Empty',
            style: GoogleFonts.quicksand(
                textStyle: TextStyle(
              fontSize: 40,
              color: Colors.white,
            ))),
      );
    }

    return Expanded(
      child: ClipRRect(
        borderRadius: BorderRadius.circular(10.0),
        child: BackdropFilter(
            filter: ImageFilter.blur(sigmaX: 3.0, sigmaY: 3.0),
            child: Container(
              child: Column(children: [
                buildUserInfo(media.user),
                Expanded(
                    child: buildPopquote(media.text)),
                //buildControls(),
              ]),
              decoration: BoxDecoration(
                color: Colors.blueAccent.withOpacity(.3),
              ),
            )),
      ),
    );
  }

  Widget buildUserInfo(User user) {
    return Container(
        margin: EdgeInsets.only(top: 0),
        color: Colors.white,
        child: Padding(
          padding: const EdgeInsets.only(top: 0.0, bottom: 0),
          child: Avatar(height: 50, width: 140, user: user, signal: .3),
        ));
  }

  Widget buildPopquote(String displayText) {
    return Padding(
      padding: const EdgeInsets.only(top: 0, left: 20, right: 20, bottom: 20),
      child: Stack(children: [
        Positioned(
          height: 30,
          width: 30,
          top: 10,
          left: 0,
          child: Image.asset(
            'assets/media/quote.png',
            fit: BoxFit.fill,
          ),
        ),
        Positioned(
          height: 30,
          width: 30,
          right: 0,
          bottom: 0,
          child: RotatedBox(
              quarterTurns: 2,
              child: Image.asset(
                'assets/media/quote.png',
                fit: BoxFit.fill,
              )),
        ),
        Padding(
          padding: EdgeInsets.only(top: 40),
          child: AutoSizeText(
            displayText,
            textAlign: TextAlign.center,
            style: GoogleFonts.quicksand(
                textStyle: TextStyle(
              fontSize: 40,
              color: Colors.white,
            )),
            maxLines: 5,
          ),
        ),
      ]),
    );
  }

  Widget buildControls() {
    return Padding(
      padding: const EdgeInsets.only(bottom: 40),
      child: Row(
        mainAxisAlignment: MainAxisAlignment.spaceEvenly,
        children: [
          IconButton(
            icon: Icon(Icons.timer),
            color: Colors.white,
            onPressed: () {},
          ),
          IconButton(
            icon: Icon(Icons.share),
            color: Colors.white,
            onPressed: () {},
          ),
        ],
      ),
    );
  }
}
