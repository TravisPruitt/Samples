import 'package:sprintf/sprintf.dart';

class Time {
  static String displayFromSeconds(int totalSeconds) {
    int minutes = totalSeconds ~/ 60;
    int seconds = totalSeconds % 60;
    return sprintf('%02d:%02d', [ minutes, seconds] );
  }
}
