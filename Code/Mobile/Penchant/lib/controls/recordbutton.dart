import 'package:flutter/material.dart';
import 'package:get/get.dart';
import 'package:google_fonts/google_fonts.dart';
import 'package:lottie/lottie.dart';
import 'package:penchant/controllers/channelcontroller.dart';
import 'package:penchant/controllers/mediacontroller.dart';

class RecordButton extends StatelessWidget {
  const RecordButton({Key key}) : super(key: key);

  @override
  Widget build(BuildContext context) {
    return Stack(
      alignment: Alignment.center,
      children: [
        ClipRRect(
          borderRadius: BorderRadius.circular(10.0),
          child: Container(
            height: 60,
            width: 320,
            decoration: BoxDecoration(
              color: Colors.lightGreen.withOpacity(.4),
              boxShadow: [
                BoxShadow(
                  color: Colors.lightGreen.withOpacity(.7),
                ),
              ],
            ),
          ),
        ),
        Positioned(
            left: 10,
            child: GestureDetector(
              child: buildButton('Amplify'),
              onTap: () {
                MediaController.to.amplify(ChannelController.to.currentUser, 5);
              },
            )),
        Positioned(
            right: 25,
            child: GestureDetector(
                onTap: () {
                  Get.to(AlertDialog(
                      title: Text('Share'),
                      content: Text('This is a button.')));
                },
                child: buildButton('Share'))),
        GestureDetector(
          onTap: () {
            if (MediaController.to.isRecording.value == false) {
              print('Starting to record...');
              MediaController.to.startRecorder();
              MediaController.to.isRecording.value = true;
            } else {
              print('Ending recording');
              MediaController.to.stopRecorder();
              MediaController.to.isRecording.value = false;
            }
          },
          child: Obx(() => Lottie.asset(
                'assets/animations/animatedrecord.json',
                animate: MediaController.to.isRecording.value,
                repeat: MediaController.to.isRecording.value,
                height: 200,
                width: 200,
              )),
        ),
      ],
    );
  }

  Widget buildButton(String text) {
    return Text(text,
        style: GoogleFonts.quicksand(
          textStyle: TextStyle(
            fontSize: 26,
            color: Colors.white,
          ),
        ));
  }
}
