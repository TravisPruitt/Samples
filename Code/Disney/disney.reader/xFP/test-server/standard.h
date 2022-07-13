/** 
    @file standard.h
    @author Greg Strange
    @date Sept 12, 2011
*/

#if !defined (__STANDARD_H__)
#    define   __STANDARD_H__



#define ArrayLength(a)  (sizeof(a) / sizeof(a[0]))

#ifdef WIN32

typedef signed char int8_t;
typedef unsigned char uint8_t;
typedef signed short int16_t;
typedef unsigned short uint16_t;
typedef signed int int32_t;
typedef unsigned int uint32_t;
typedef signed __int64 int64_t;
typedef unsigned __int64 uint64_t;

typedef long ssize_t;

#define strcasecmp(a,b)  _stricmp(a,b)
#define strncasecmp(a,b,n)  _strnicmp(a,b,n)
#define snprintf(a, ...)   _snprintf(a, __VA_ARGS__)

#endif



#endif
