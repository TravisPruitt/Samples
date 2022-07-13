
#include "character.h"



Byte character_toLower (Byte c)
{
    if (('A' <= c) && (c <= 'Z'))
        return c - 'A' + 'a' ;
    else
        return c ;
}



Boolean character_isPrintable (Byte c)
{
    return (c >= '!') && (c <= '~');
}



Boolean character_isWhitespace (Byte c)
{

#if 1
    return (c != 0) && ! character_isPrintable (c);
#else
    switch (c)
    {
        case ' '  :
        case '\t' :
        case '\r' :
        case '\n' :     return TRUE;

        default :       return FALSE;
    }
#endif

}



Boolean character_isHexDigit (Byte c)
{
Byte digit ;

    digit = character_toLower (c) ;

    return (('0' <= digit) && (digit <= '9')) ||
           (('a' <= digit) && (digit <= 'f')) ;
}



Byte character_hexValue (Byte c)
{
Byte digit ;

    digit = c ;
    if (('0' <= digit) && (digit <= '9'))
        return digit - '0' ;

    digit = character_toLower (digit) ;
    if (('a' <= digit) && (digit <= 'f'))
        return digit - 'a' + 10 ;

    return 0 ;
}
