#ifndef _TYPES_H_
#define _TYPES_H_


typedef           char          Signed_8 ;
typedef  unsigned char        Unsigned_8 ;

typedef           short        Signed_16 ;
typedef  unsigned short      Unsigned_16 ;

typedef           int          Signed_32 ;
typedef  unsigned int        Unsigned_32 ;

typedef           long long    Signed_64 ;
typedef  unsigned long long  Unsigned_64 ;


typedef Unsigned_8  Byte ;
typedef Unsigned_16 Word ;
typedef Byte        Boolean ;
typedef float       Real ;


#define FALSE        0
#define TRUE  (! FALSE)


#define ArrayLength(anArray)  (sizeof(anArray) / sizeof(anArray[0]))


#endif
