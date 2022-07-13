#ifndef READER_H
#define READER_H

#include <stdint.h>
#include <sys/time.h>
#include "inputstream.h"

enum DeviceType {
    DeviceTypeUnknown = 0,
    DeviceTypeXBR = 1,
    DeviceTypeXTP = 2,
    DeviceTypeCar = 3,
    DeviceTypeXtpGpio = 4
};

struct Options
{
    const char *pszConfigFile;
    const char *pszControllerName;
    const char *pszReaderName;
    int bVerbose;
    int bUseRealMac;
};

struct Reader;

int CreateReadersFromConfiguration(struct Options *pOptions, struct Reader ***pppReaders, size_t *psReaders);

int CreateReader(
    const char *pszName,
    enum DeviceType deviceType,
    const char *pszHardwareAddress,
    const char *pszHardwareType,
    uint16_t Port,         // 0 means no web server
    const char *pszMac,
    const char *pszControllerURL,
    const char *pszXBRMSURL,
    int x,
    int y,
    const char *pszInputFilename,
    enum InputType InputType,
    long long start,
    long long end,
    struct Reader **ppReader);

int StartReader(struct Reader *pReader, struct timeval *tvNow);

int StopReader(struct Reader *pReader);

int DeleteReader(struct Reader *pReader);

const char *ReaderName(struct Reader *pReader);
enum DeviceType ReaderType(struct Reader *pReader);
uint16_t ReaderPort(struct Reader *pReader);
void ReaderStats(
    struct Reader *pReader,
    int *bFailedLastSend,
    unsigned long long *goodSendCount,
    unsigned long long *badSendCount,
    int *bFailedLastHello);

const char* GetReaderType(enum DeviceType deviceType);
#endif
