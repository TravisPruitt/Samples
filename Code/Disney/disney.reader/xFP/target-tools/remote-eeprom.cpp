/**
    @file   remote-eeprom.h 
    @date   September 2012

    Copyright (c) 2012, synapse.com

    Simple program to write values to the Camilla EEPROM during manufacturing
*/


#include <stdint.h>
#include <sys/stat.h>
#include <fcntl.h>
#include <unistd.h>
#include <errno.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <math.h>
#include <string>

#include "RemoteEEPROM.h"


#define EEPROM_FILE_NAME    "/sys/devices/platform/omap/omap_i2c.3/i2c-3/3-0050/eeprom"

#define VERSION "1"




bool readEEPROM(struct RemoteEEPROM& eeprom)
{
    int fd = open(EEPROM_FILE_NAME, O_RDONLY);
    if (fd < 0)
    {
        printf("Unable to open EEPROM for reading, error %d\n", errno);
        exit(1);
    }

    int result = read(fd, &eeprom, sizeof(eeprom));
    if (result != sizeof(eeprom))
    {
        if (result >= 0)
            printf("read operation read unexpected length %d, expected %d\n", sizeof(eeprom), result);
        else
            printf("read operation failed, error %d\n", errno);
        close(fd);
        return false;
    }

    close(fd);

    return true;
}



std::string getString(const char* bytes, size_t length)
{
    char buf[length+1];

    size_t i;
    for (i = 0; (i < length) && (bytes[i] != 0) && (bytes[i] != 0xff); ++i)
        buf[i] = bytes[i];
    buf[i] = 0;
    return std::string(buf);
}



void dumpEEPROM(struct RemoteEEPROM& info)
{
    struct CamillaEEPROM_1& cam = info.u.camilla;

    printf("    format              = %d\n", info.format);
    printf("    hardware            = '%s'\n", getString(info.u.camilla.description, sizeof(info.u.camilla.description)).c_str());
    printf("    serial number       = '%s'\n", getString(info.u.camilla.serialNumber, sizeof(info.u.camilla.serialNumber)).c_str());
    printf("    build info          = '%s'\n", getString(info.u.camilla.buildInfo, sizeof(info.u.camilla.buildInfo)).c_str());
    printf("    red slope, offset   = %f, %f\n", cam.redSlope, cam.redOffset);
    printf("    green slope, offset = %f, %f\n", cam.greenSlope, cam.greenOffset);
    printf("    blue slope, offset  = %f, %f\n", cam.blueSlope, cam.blueOffset);
}


bool writeEEPROM(struct RemoteEEPROM& eeprom)
{
    // TODO - do I need to erase it first?
    int fd = open(EEPROM_FILE_NAME, O_WRONLY);
    if (fd < 0)
    {
        printf("Unable to open EEPROM for writing, error %d\n", errno);
        return false;
    }

    int result = write(fd, &eeprom, sizeof(eeprom));
    if (result != sizeof(eeprom))
    {
        if (result >= 0)
            printf("Unable to write all bytes to EEPROM\n");
        else
            printf("Error writing to EEPROM, error %d\n", errno);
        close(fd);
        return false;
    }

    close(fd);
    return true;
}


bool eraseEEPROM()
{
    int fd = open(EEPROM_FILE_NAME, O_WRONLY);
    if (fd < 0)
    {
        printf("Unable to open EEPROM for writing, error %d\n", errno);
        return false;
    }

    uint8_t ffs[512];
    memset(ffs, 0xff, sizeof(ffs));

    int result;
    size_t bytesWritten = 0;
    do
    {
        result = write(fd, ffs, sizeof(ffs));
        if (result > 0)
            bytesWritten += result;
    }
    while (result == sizeof(ffs));
    printf("Erase finished, result = %d\n", result);

    printf("Erased %d bytes\n", bytesWritten);

    if (result < 0)
    {
        printf("Error writing to EEPROM, error %d\n", errno);
        close(fd);
        return false;
    }

    close(fd);
    return true;
}



void usage()
{
    printf("\n");
    printf("usage:\n");
    printf("    remote-eeprom [options]\n");
    printf("\n");
    printf("options:\n");
    printf("    -b <build info>      Set the build info (text up to 64 characters)\n");
    printf("    -e                   Erase EEPROM\n");
    printf("    -s <serial number>   Set the serial number (text up to 32 characters)\n");
    printf("    -R <red values>      Red calibration values (slope and offset)\n");
    printf("    -G <green values>    Green calibration values (slope and offset)\n");
    printf("    -B <blue values>     Blue calibration values (slope and offset)\n");
    printf("example:\n");
    printf("    remote-eeprom -s 12345689ahxy -b \"9/28/2012 run 2\" -R 0.6 2 -G 1 0 -B 0.8 1\n");
    printf("\n");
}




bool checkSlopeOffset(float slope, float offset)
{
    if (slope <= 0 || slope > 1.0)
    {
        printf("Slope must be > 0 and <= 1.0\n");
        return false;
    }
    if (offset < 0 || offset > 255)
    {
        printf("Offset must be >= 0 and < 256\n");
        return false;
    }
    return true;
}



int main(int argc, char** argv)
{
    printf("remote-eeprom version "VERSION"\n");

    struct RemoteEEPROM originalValues;
    struct RemoteEEPROM newValues;
    bool updateEEPROM = false;
    bool erase = false;

    if (!readEEPROM(originalValues))
    {
        printf("Error reading eeprom\n");
        exit(1);
    }

    newValues = originalValues;

    // parse command line arguements
    for (int i = 1; i < argc; ++i)
    {
        char* arg = argv[i];

        if (*arg == '-')
        {
            ++arg;
            switch (*arg)
            {
            case 'b':
                if (++i >= argc)
                {
                    printf("No build info given\n");
                    exit(0);
                }
                strncpy(newValues.u.camilla.buildInfo, argv[i], sizeof(newValues.u.camilla.buildInfo));
                updateEEPROM = true;
                break;

            case 'e':
                erase = true;
                break;
                
            case 's':
                if (++i >= argc)
                {
                    printf("No serial number given\n");
                    exit(0);
                }
                strncpy(newValues.u.camilla.serialNumber, argv[i], sizeof(newValues.u.camilla.serialNumber));
                updateEEPROM = true;
                ++i;
                break;

            case 'R':
                if (i+2 >= argc)
                {
                    printf("Need slope and offset values\n");
                    exit(0);
                }
                newValues.u.camilla.redSlope = atof(argv[++i]);
                newValues.u.camilla.redOffset = atof(argv[++i]);
                if (!checkSlopeOffset(newValues.u.camilla.redSlope, newValues.u.camilla.redOffset))
                    exit(0);
                updateEEPROM = true;
                break;

            case 'G':
                if (i+2 >= argc)
                {
                    printf("Need slope and offset values\n");
                    exit(0);
                }
                newValues.u.camilla.greenSlope = atof(argv[++i]);
                newValues.u.camilla.greenOffset = atof(argv[++i]);
                if (!checkSlopeOffset(newValues.u.camilla.greenSlope, newValues.u.camilla.greenOffset))
                    exit(0);
                updateEEPROM = true;
                break;

            case 'B':
                if (i+2 >= argc)
                {
                    printf("Need slope and offset values\n");
                    exit(0);
                }
                newValues.u.camilla.blueSlope = atof(argv[++i]);
                newValues.u.camilla.blueOffset = atof(argv[++i]);
                if (!checkSlopeOffset(newValues.u.camilla.blueSlope, newValues.u.camilla.blueOffset))
                    exit(0);
                updateEEPROM = true;
                break;

            default:
                usage();
                exit(0);
            }
        }
    }

    printf("EEPROM contents:\n");
    dumpEEPROM(originalValues);

    if (erase)
    {
        if (!eraseEEPROM())
            printf("Error erasing eeprom\n");
        else
            printf("EEPROM erased\n");
    }

    if (updateEEPROM)
    {
        // These fields are fixed and cannot be set by user
        newValues.format = RemoteEEPROM_format_Camilla_1;
        strncpy(newValues.u.camilla.description, "xTPX rev 1", sizeof(newValues.u.camilla.description));

        if (!writeEEPROM(newValues))
        {
            printf("Error writing eeprom\n");
            exit(1);
        }

        printf("New EEPROM contents:\n");
        dumpEEPROM(newValues);
    }

    exit(0);
}
