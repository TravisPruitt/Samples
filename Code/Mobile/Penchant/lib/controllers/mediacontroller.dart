import 'dart:async';
import 'dart:io';

import 'package:firebase_storage/firebase_storage.dart';
import 'package:flutter_sound/flutter_sound.dart';
import 'package:get/get.dart';
import 'package:path_provider/path_provider.dart';
import 'package:penchant/model/media.dart';
import 'package:penchant/model/user.dart';
import 'package:penchant/services/dataservice.dart';
import 'package:permission_handler/permission_handler.dart';

class MediaController extends GetxController {
  static MediaController to = Get.find();
  RxString _activeChannel = "".obs;
  var _queue = List<Media>().obs;
  FlutterSoundPlayer _player = FlutterSoundPlayer();
  FlutterSoundRecorder _recorder = FlutterSoundRecorder();
  StreamSubscription _recorderSubscription;
  final Codec _codec = Codec.flac;
  double _lastDB = 0;
  RxBool isRecording = false.obs;
  String _localPath;

  @override
  void onInit() {
    super.onInit();
  }

  set activeChannel(String channelId) {
    _activeChannel.value = channelId.toLowerCase();
    print('Refreshing data for channel: ${channelId.toLowerCase()}');

    DataService.to.streamQueue(_activeChannel.value).listen((snapshot) {
      if (snapshot != null) {
        _queue.value = Media.fromDocumentsSnapshot(snapshot);
      } else {
        _queue.value = null;
      }
    });
  }

  Media get currentMedia {
    Media currentMedia;

    if (_queue.isNotEmpty) {
      currentMedia = _queue[0];
    }

    return currentMedia;
  }

  List<Media> get currentQueue {
    return _queue.value;
  }

  void amplify(User fromUser, int seconds) {
    // TODO: Need to put this in some sort of transaction.
    if (currentMedia != null && currentMedia.user != null) {
      currentMedia.user.availableTime += 5;
      DataService.to.updateUserTime(
          currentMedia.user.id, currentMedia.user.availableTime);

      fromUser.availableTime -= 5;
      DataService.to.updateUserTime(fromUser.id, fromUser.availableTime);
    }
  }

  Future<void> playMedia(
      String userId, String channelId, String location) async {
    print('Queing for listening: $location');

    _queue.add(Media());
    update();

    //await startPlayer();
  }

  Future<void> startPlayer() async {
    await _player.openAudioSession();

    Get.snackbar('Player', 'Added to queue');

    await _player.startPlayer(
      fromURI: _queue[0].media,
      codec: Codec.flac,
      whenFinished: () async {
        print('Finished playing');
        await _player.closeAudioSession();
      },
    );
  }

  void startRecorder() async {
    try {
      isRecording.value = true;

      // Request Microphone permission if needed
      PermissionStatus status = await Permission.microphone.request();
      if (status != PermissionStatus.granted) {
        throw RecordingPermissionException("Microphone permission not granted");
      }

      _recorder.openAudioSession(
          focus: AudioFocus.requestFocusTransient,
          category: SessionCategory.playAndRecord,
          mode: SessionMode.modeDefault,
          device: AudioDevice.speaker);

      _localPath = await localPath();
      await _recorder.startRecorder(
        toFile: _localPath,
        codec: _codec,
        bitRate: 16000,
        sampleRate: 16000,
        audioSource: AudioSource.voice_communication,
      );
      print('Starting recordint at $_localPath');

      _recorderSubscription = _recorder.onProgress.listen((e) {
        if (e != null && e.duration != null) {
          if ((e.decibels - _lastDB).abs() >= 1) {
            print('${e.decibels}.');
          }

          _lastDB = e.decibels;
        }
      });
    } catch (err) {
      print('startRecorder error: $err');
      stopRecorder();
    }
  }

  void stopRecorder() async {
    try {
      await _recorder.stopRecorder();
      print('Stopping recording');
      cancelRecorderSubscriptions();

      print('Adding message to firebase.');
      String messageId = await DataService.to.addMedia();
      print('Added message to firebase with id: $messageId');

      print('Starting upload');
      String remoteMedia = await uploadFile(_localPath, messageId);
      print('uploaded successful');

      // Update local record.
      await DataService.to.updateMedia(messageId, remoteMedia);
      await DataService.to.addMediaTag(messageId, _activeChannel.value);
    } catch (err) {
      print('stopRecorder error: $err');
    }

    isRecording.value = false;
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

  Future<String> localPath() async {
    Directory tempDir = await getTemporaryDirectory();
    String localPath = '${tempDir.path}/penchant.flac';
    return localPath;
  }

  void cancelRecorderSubscriptions() {
    if (_recorderSubscription != null) {
      _recorderSubscription.cancel();
      _recorderSubscription = null;
    }
  }
}
