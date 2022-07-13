import 'dart:async';
import 'dart:math';

import 'package:get/get.dart';
import 'package:penchant/controllers/mediacontroller.dart';
import 'package:penchant/model/channel.dart';
import 'package:penchant/model/signal.dart';
import 'package:penchant/model/user.dart';
import 'package:penchant/services/dataservice.dart';

class ChannelController extends GetxController {
  static ChannelController to = Get.find();

  var _channels = List<Channel>().obs;
  var _signals = List<Signal>().obs;
  RxBool _loggedIn = false.obs;
  RxBool previewVisible = false.obs;
  Rx _previewChannel = Rx();
  Rx _currentUser = Rx();
  Timer _timer;

  void onInit() {
    DataService.to.streamChannels().listen((snapshot) {
      if (snapshot != null) {
        snapshot.documents.forEach((element) {
          _channels.add(Channel.fromDocumentSnapshot(element));
        });

        if (_channels.length > 0) {
          MediaController.to.activeChannel = _channels[0].name;
        }
      }
    });

    _timer = Timer.periodic(Duration(milliseconds: 10000), (timer) {
      Random random = new Random();
      List<Signal> signals = List<Signal>();

      // Generate a random number of signals.
      for (var i = 0; i < random.nextInt(10); i++) {
        signals.add(Signal(random.nextDouble()));
      }

      print('Refreshing signals. ${signals.length} added.');

      _signals.value = signals;
      //update();
    });

    super.onInit();
  }

  void onClose() {
    if (_timer != null) {
      _timer.cancel();
      _timer = null;
    }

    super.onClose();
  }

  Channel getChannel(String channelId) {
    Channel foundChannel;

    foundChannel = _channels.where((element) => element.id == channelId).first;

    return foundChannel;
  }

  List<Channel> get channels {
    return _channels.value;
  }

  List<Signal> get signals {
    return _signals.value;
  }

  void setCurrentUser(String userId) {
    DataService.to.streamUser(userId).listen((snapshot) {
      if (snapshot != null) {
        _currentUser.value = User.fromDocumentSnapshot(snapshot);
        _loggedIn.value = true;
      }
    });
  }

  User get currentUser {
    return _currentUser.value;
  }

  Channel get previewChannel {
    return _previewChannel.value;
  }

  set previewChannel(Channel previewChannel) =>
      _previewChannel.value = previewChannel;

  bool get loggedIn {
    return _loggedIn.value;
  }
}
