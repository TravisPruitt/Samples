import 'package:flutter/material.dart';
import 'package:get/get.dart';
import 'controllers/channelcontroller.dart';
import 'controllers/mediacontroller.dart';
import 'screens/home.dart';
import 'screens/login.dart';
import 'services/dataservice.dart';
import 'package:flutter/services.dart';

void main() {
  WidgetsFlutterBinding.ensureInitialized();

  SystemChrome.setSystemUIOverlayStyle(SystemUiOverlayStyle(
          statusBarBrightness: Brightness.dark) // Or Brightness.dark
      );

  runApp(GetMaterialApp(
      title: 'Penchant Real Time Sentiment',
      debugShowCheckedModeBanner: false,
      theme: ThemeData(
        backgroundColor: Colors.black,
        visualDensity: VisualDensity.adaptivePlatformDensity,
      ),
      home: PenchantApp()));
}

class PenchantApp extends StatelessWidget {
  final DataService dataService = Get.put<DataService>(DataService());
  final MediaController mediaController =
      Get.put<MediaController>(MediaController());
  final ChannelController channelController =
      Get.put<ChannelController>(ChannelController());

  @override
  Widget build(BuildContext context) {
    return Obx(() => ChannelController.to.loggedIn == false ? LoginPage() : HomePage());
  }
}
