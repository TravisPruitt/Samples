#ifndef _CHARACTER_H_
#define _CHARACTER_H_


#include "types.h"


#define Control_C    3
#define Backspace    8
#define Control_J   10
#define Control_M   13
#define Control_Z   26
#define Escape      27


Byte    character_toLower      (Byte) ;

Boolean character_isPrintable  (Byte) ;
Boolean character_isWhitespace (Byte) ;

Boolean character_isHexDigit   (Byte) ;
Byte    character_hexValue     (Byte) ;


#endif
