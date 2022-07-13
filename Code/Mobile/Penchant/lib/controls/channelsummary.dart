import 'dart:ui';
import 'package:auto_size_text/auto_size_text.dart';
import 'package:flutter/material.dart';
import 'package:get/get.dart';
import 'package:google_fonts/google_fonts.dart';
import 'package:penchant/controllers/channelcontroller.dart';
import 'package:penchant/model/channel.dart';

class ChannelSummary extends StatelessWidget {
  final String channelId;

  const ChannelSummary({Key key, @required this.channelId}) : super(key: key);

  @override
  Widget build(BuildContext context) {
    return Obx(() {
      Channel channel = ChannelController.to.getChannel(channelId);

      return Padding(
        padding: const EdgeInsets.only(left: 10),
        child: ClipRRect(
          borderRadius: BorderRadius.circular(10.0),
          child: BackdropFilter(
            filter: ImageFilter.blur(sigmaX: 3.0, sigmaY: 3.0),
            child: Container(
              width: 355,
              height: 90,
              child: Column(
                children: [
                  Row(children: [buildTitle(channel)]),
                  Row(children: [buildSummary(channel)]),
                ],
              ),
              decoration: BoxDecoration(
                color: Colors.blueAccent.withOpacity(.3),
              ),
            ),
          ),
        ),
      );
    });
  }

  buildSummary(Channel channel) {
    return Padding(
      padding: const EdgeInsets.only(left: 8, top: 5),
      child: Text(channel.summary, style: GoogleFonts.quicksand(
              textStyle: TextStyle(
                fontSize: 20,
                color: Colors.white,
              ),
            )),
    );
  }

  buildTitle(Channel channel) {
    return Padding(
      padding: const EdgeInsets.only(left: 10),
      child: AutoSizeText(channel.name,
          style: GoogleFonts.quicksand(
            textStyle: TextStyle(
              fontSize: 40,
              fontWeight: FontWeight.bold,
              color: Colors.white,
            ),
          )),
    );
  }
}
