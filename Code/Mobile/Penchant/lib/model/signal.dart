import 'package:flutter/material.dart';

class Signal {
  Signal(this.strength);

  double strength;

  Color get color {
    Color strengthColor;

    if (strength < .5) {
      strengthColor = Colors.red;
    } else {
      strengthColor = Colors.green;
    }

    return strengthColor;
  }
}
