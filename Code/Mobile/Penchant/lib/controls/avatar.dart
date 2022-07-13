import 'package:auto_size_text/auto_size_text.dart';
import 'package:extended_image/extended_image.dart';
import 'package:flutter/material.dart';
import 'package:google_fonts/google_fonts.dart';
import 'package:penchant/model/user.dart';
import 'package:penchant/utils/time.dart';

class Avatar extends StatelessWidget {
  const Avatar(
      {Key key,
      this.height,
      this.width,
      @required this.user,
      @required this.signal})
      : super(key: key);

  final User user;
  final double height;
  final double width;
  final double signal;

  @override
  Widget build(BuildContext context) {
    return Container(
      child: Stack(children: [
        buildAvatarInfo(user),
        buildAvatar(user),
      ]),
    );
  }

  buildAvatar(User user) {
    if (user == null) {
      return Text('Loading');
    }

    if (user.mediaUrl == null) {
      return Text('No image');
    }

    return Align(
      alignment: Alignment.topLeft,
      child: ClipRRect(
        borderRadius: BorderRadius.circular(height),
        child:
            ExtendedImage.network(user.mediaUrl, width: height, height: height),
      ),
    );
  }

  buildAvatarInfo(User user) {
    if (user == null) {
      return Text('Loading');
    }

    return ClipRRect(
      borderRadius: BorderRadius.circular(10.0),
      child: Container(
        margin: EdgeInsets.only(left: height / 2),
        padding: EdgeInsets.all(5),
        width: width,
        height: height,
        color: Colors.white,
        child: Row(
          mainAxisAlignment: MainAxisAlignment.end,
          crossAxisAlignment: CrossAxisAlignment.center,
          children: [
            Padding(
              padding: const EdgeInsets.all(1.0),
              child: AutoSizeText(Time.displayFromSeconds(user.availableTime),
                  style: GoogleFonts.quicksand(
                      textStyle: TextStyle(
                    fontSize: height,
                    color: Colors.black,
                  ))),
            ),
            Icon(Icons.wifi,
                size: height * .5,
                color: signal > .5 ? Colors.green : Colors.red),
          ],
        ),
      ),
    );
  }

  getChipStyle() {
    return TextStyle(fontSize: 10);
  }
}
