import 'dart:async';
import 'dart:io';
import 'package:firebase_storage/firebase_storage.dart';
import 'package:flutter/material.dart';
import 'package:path_provider/path_provider.dart';
import 'package:flutter_sound/flutter_sound.dart';
import 'package:permission_handler/permission_handler.dart';

class RecordPage extends StatelessWidget {
  final FlutterSoundRecorder soundRecorder = FlutterSoundRecorder();
  StreamSubscription _recorderSubscription;
  FlutterSoundRecorder recorderModule = FlutterSoundRecorder();
  final Codec _codec = Codec.flac;
  double _lastDB = 0;
  //String _path;
  bool _isRecording = false;

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: Text('Record page'),
      ),
      body: Row(
        //crossAxisAlignment: CrossAxisAlignment.center,
        mainAxisAlignment: MainAxisAlignment.center,
        children: <Widget>[
          RaisedButton(
            child: Text('Start Recording'),
            onPressed: () async {
              if (_isRecording == false) {
                try {
                  print('Recording started');

                  startRecorder();

                  _isRecording = true;
                } catch (err) {
                  print('Error starting to record: $err');
                }
              }
            },
          ),
          SizedBox(
            width: 5,
          ),
          RaisedButton(
            child: Text('End Recording'),
            onPressed: () async {
              stopRecorder(await localPath());
            },
          )
        ],
      ),
    );
  }

  Future<String> localPath() async {
      Directory tempDir = await getTemporaryDirectory();
      String localPath ='${tempDir.path}/penchant.flac';
      return localPath;
  }

  void startRecorder() async {
    try {
      // Request Microphone permission if needed
      PermissionStatus status = await Permission.microphone.request();
      if (status != PermissionStatus.granted) {
        throw RecordingPermissionException("Microphone permission not granted");
      }

      recorderModule.openAudioSession(
          focus: AudioFocus.requestFocusTransient,
          category: SessionCategory.playAndRecord,
          mode: SessionMode.modeDefault,
          device: AudioDevice.speaker);

      await recorderModule.startRecorder(
        toFile: await localPath(),
        codec: _codec,
        bitRate: 16000,
        sampleRate: 16000,
        audioSource: AudioSource.voice_communication,
      );
      print('Starting recordint at $localPath');

      _recorderSubscription = recorderModule.onProgress.listen((e) {
        if (e != null && e.duration != null) {
          if ((e.decibels - _lastDB).abs() >= 1) {
            print('${e.decibels}.');
          }

          _lastDB = e.decibels;
        }
      });
    } catch (err) {
      print('startRecorder error: $err');
      stopRecorder(await localPath());
    }
  }

  void stopRecorder(String localPath) async {
    try {
      await recorderModule.stopRecorder();
      print('Stopping recording');
      cancelRecorderSubscriptions();

      print('Adding message to firebase.');
      // TODO: String messageId = await MediaController.to.addMedia();
      //print('Added message to firebase with id: $messageId');

      print('Starting upload');
      //String remoteMedia = await uploadFile(localPath, messageId);
      print('uploaded successful');

      // Update local record.
      // TODO: await MediaController.to.updateMedia(messageId, remoteMedia);
    } catch (err) {
      //print('stopRecorder error: $err');
    }

    _isRecording = false;
  }

  void cancelRecorderSubscriptions() {
    if (_recorderSubscription != null) {
      _recorderSubscription.cancel();
      _recorderSubscription = null;
    }
  }

  Future<String> uploadFile(String localPath, String documentId) async {
    String remotePath;

    try {
      StorageReference storageRef =
          FirebaseStorage.instance.ref().child('recordings/$documentId.flac');
      File audioFile = File.fromUri(Uri(path: localPath));

      StorageTaskSnapshot snapshot =
          await storageRef.putFile(audioFile).onComplete;

      remotePath = await snapshot.ref.getDownloadURL();
      print('File Uploaded: ' + remotePath);
    } catch (err) {
      print("Can't upload file: " + err.details);
    }

    return remotePath;
  }
}
