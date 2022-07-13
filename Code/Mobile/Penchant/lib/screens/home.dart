import 'package:extended_image/extended_image.dart';
import 'package:flutter/material.dart';
import 'package:flutter_multi_carousel/carousel.dart';
import 'package:get/get.dart';
import 'package:google_fonts/google_fonts.dart';
import 'package:penchant/controllers/channelcontroller.dart';
import 'package:penchant/controllers/mediacontroller.dart';
import 'package:penchant/controls/avatar.dart';
import 'package:penchant/controls/channelpreview.dart';
import 'package:penchant/controls/channelselector.dart';
import 'package:penchant/controls/channelsummary.dart';
import 'package:penchant/controls/channelplayingnow.dart';
import 'package:penchant/controls/recordbutton.dart';
import 'package:penchant/model/channel.dart';
import 'package:penchant/model/user.dart';
import 'package:sliding_up_panel/sliding_up_panel.dart';

class HomePage extends StatefulWidget {
  HomePage({Key key, this.title}) : super(key: key);
  final String title;

  @override
  _HomePageState createState() => _HomePageState();
}

class _HomePageState extends State<HomePage> {
  bool _recordVisible = true;

  @override
  Widget build(BuildContext context) {
    return SafeArea(
        child: Scaffold(
            floatingActionButtonLocation:
                FloatingActionButtonLocation.centerFloat,
            floatingActionButton:
                Visibility(visible: _recordVisible, child: RecordButton()),
            backgroundColor: Colors.black,
            body: SlidingUpPanel(
              onPanelSlide: (panelPosition) {
                setState(() {
                  _recordVisible = false;
                });
              },
              onPanelClosed: () {
                setState(() {
                  _recordVisible = true;
                });
              },
              renderPanelSheet: false,
              minHeight: 60,
              maxHeight: 520,
              collapsed: buildCollapsedQueue(),
              panel: buildExpandedQueue(),
              body: Obx(() {
                if (ChannelController.to.channels.length > 0) {
                  return Stack(children: [
                    buildCarousel(context, ChannelController.to.channels),
                    Padding(
                        padding: EdgeInsets.only(top: 20, right: 10),
                        child: Row(
                          mainAxisAlignment: MainAxisAlignment.end,
                          children: [
                            ChannelController.to.currentUser != null
                                ? buildAvatar(ChannelController.to.currentUser)
                                : CircularProgressIndicator(),
                          ],
                        )),
                    Positioned(
                        right: 10,
                        top: 120,
                        child: ChannelSelector(height: 360, width: 60)),
                    Positioned(
                        top: 200,
                        left: 80,
                        child: Visibility(
//                            visible: ChannelController.to.previewChannel != null,
                            visible: ChannelController.to.previewVisible.value,
                            child: ChannelPreview(
                                channel: ChannelController.to.previewChannel)))
                  ]);
                } else {
                  return CircularProgressIndicator();
                }
              }),
            )));
  }

  buildCollapsedQueue() {
    return Container(
      decoration: BoxDecoration(
        color: Colors.black,
        borderRadius: BorderRadius.only(
            topLeft: Radius.circular(20.0), topRight: Radius.circular(20.0)),
      ),
      margin: const EdgeInsets.fromLTRB(10.0, 10.0, 10.0, 0.0),
      child: Padding(
        padding: const EdgeInsets.only(top: 10),
        child: Row(
          mainAxisAlignment: MainAxisAlignment.spaceEvenly,
          children: [
            Column(
              children: [
                Text("Now Playing",
                    style: GoogleFonts.quicksand(
                      textStyle: TextStyle(
                        fontSize: 24,
                        color: Colors.white,
                      ),
                    )),
              ],
            ),
            Column(
              children: [
                Row(
                  children: [
                    Icon(
                      Icons.skip_next,
                      color: Colors.white,
                      size: 36,
                    ),
                    Icon(
                      Icons.play_arrow,
                      color: Colors.white,
                      size: 36,
                    ),
                  ],
                ),
              ],
            ),
          ],
        ),
      ),
    );
  }

  buildExpandedQueue() {
    return Container(
      decoration: BoxDecoration(
        color: Colors.black,
        borderRadius: BorderRadius.only(
            topLeft: Radius.circular(20.0), topRight: Radius.circular(20.0)),
      ),
      margin: const EdgeInsets.fromLTRB(10.0, 10.0, 10.0, 0.0),
      child: Padding(
          padding: const EdgeInsets.all(10.0),
          child: Obx(
            () => Column(children: [
              Text(
                "Now Playing Queue",
                style: GoogleFonts.quicksand(
                  textStyle: TextStyle(
                    fontSize: 20,
                    color: Colors.white,
                  ),
                ),
              ),
              for (var item in MediaController.to.currentQueue)
                Row(
                  children: [
                    CircleAvatar(
                      backgroundImage: NetworkImage(
                          'https://www.pngkit.com/png/full/133-1337492_google-employee-amanda-avatar.png'),
                    ),
                    Padding(
                      padding: const EdgeInsets.all(8.0),
                      child: Text(
                        item.text,
                        style: TextStyle(color: Colors.white),
                      ),
                    )
                  ],
                )
            ]),
          )),
    );
  }

  buildCarousel(context, channels) {
    return Carousel(
        height: MediaQuery.of(context).size.height -
            MediaQuery.of(context).padding.top -
            MediaQuery.of(context).padding.bottom -
            20,
        width: MediaQuery.of(context).size.width,
        initialPage: 0,
        allowWrap: true,
        type: Types.simple,
        onCarouselTap: (i) {
          MediaController.to.activeChannel = channels[i].name;
        },
        onPageChange: () {
          print('onPageChage');
        },
        showIndicator: false,
        indicatorType: IndicatorTypes.dot,
        arrowColor: Colors.black,
        axis: Axis.horizontal,
        showArrow: false,
        children: [for (var channel in channels) buildChannel(channel)]);
  }

  Widget buildChannel(Channel channel) {
    return Container(
      color: Colors.black,
      child: Stack(
        children: [
          buildBackground(channel),
          Column(children: [
            // Channel summary and avatar
            Padding(
              padding: const EdgeInsets.only(top: 0),
              child: Row(
                mainAxisAlignment: MainAxisAlignment.spaceBetween,
                children: [ChannelSummary(channelId: channel.id)],
              ),
            ),
            Expanded(
                child: Padding(
              padding: const EdgeInsets.only(
                  top: 10, left: 10, right: 85, bottom: 170),
              child: Row(children: [
                ChannelPlayingNow(media: MediaController.to.currentMedia)
              ]),
            )),
          ]),
        ],
      ),
    );
  }

  Widget buildAvatar(User user) {
    return Avatar(width: 140, height: 50, user: user, signal: .7);
  }

  Widget buildBackground(channel) {
    return Opacity(
        opacity: 1,
        child: ExtendedImage.network(
          channel.cover,
          fit: BoxFit.cover,
          height: double.infinity,
          width: double.infinity,
        ));
  }
}
