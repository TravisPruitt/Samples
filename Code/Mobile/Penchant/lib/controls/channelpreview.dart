import 'dart:ui';

import 'package:flutter/material.dart';
import 'package:google_fonts/google_fonts.dart';
import 'package:penchant/controllers/mediacontroller.dart';
import 'package:penchant/controls/channelplayingnow.dart';
import 'package:penchant/model/channel.dart';

class ChannelPreview extends StatelessWidget {
  final Channel channel;

  const ChannelPreview({Key key, @required this.channel}) : super(key: key);

  @override
  Widget build(BuildContext context) {
    return BackdropFilter(
        filter: ImageFilter.blur(sigmaX: 3.0, sigmaY: 3.0),
        child: Container(
          height: 200,
          width: 200,
          //color: Colors.white,
          decoration: BoxDecoration(
            color: Colors.blueAccent.withOpacity(.3),
          ),
//      child: Text(Get.find<ChannelController>().previewId.value),
          child: Container(
              padding: EdgeInsets.only(top: 0, left: 2, right: 2, bottom: 2),
              color: Colors.black,
              child: Column(
                children: [
                  Text(channel.name,
                      style: GoogleFonts.quicksand(
                        textStyle: TextStyle(
                          fontSize: 12,
                          color: Colors.white,
                        ),
                      )),
                  ChannelPlayingNow(
                    media: MediaController.to.currentMedia,
                  ),
                ],
              )),
        ));
  }
}
