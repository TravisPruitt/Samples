import 'package:flutter/material.dart';
import 'package:get/get.dart';
import 'package:penchant/controllers/channelcontroller.dart';
import 'package:penchant/model/signal.dart';

class ChannelSelector extends StatelessWidget {
  final double height;
  final double width;

  const ChannelSelector({Key key, @required this.height, @required this.width})
      : super(key: key);
  @override
  Widget build(BuildContext context) {
    return GestureDetector(
        onVerticalDragStart: (details) {
          ChannelController.to.previewVisible.value = true;
          print('drag started.');
        },
        onVerticalDragEnd: (details) {
          ChannelController.to.previewVisible.value = false;
          print('drag ended.');
        },
        onVerticalDragUpdate: (details) {
          int channelIndex = 0;

          channelIndex = details.localPosition.dy ~/
              (height ~/ ChannelController.to.channels.length);
          print('Channel index: $channelIndex');

          ChannelController.to.previewChannel =
              ChannelController.to.channels[channelIndex];
          print('drag updated.');
        },
        onHorizontalDragEnd: (details) {
          print('horizontal drag ended.');
        },
        child: ClipRRect(
          borderRadius: BorderRadius.circular(10),
                  child: Container(
                decoration: BoxDecoration(
                  color: Colors.black.withOpacity(.4),),
            child: Obx(() => buildWaveform(ChannelController.to.signals)),
            height: height,
            width: width,
          ),
        ));
  }

  Widget buildWaveform(List<Signal> signals) {
    double currentTop = -30;

    return Stack(
      children: [
        for (var signal in signals)
          Positioned(
            top: currentTop += 30,
            left: 10,
            child: Icon(
              Icons.wifi,
              color: signal.color,
              size: 36,
            ),
          )
      ],
    );
  }
}
