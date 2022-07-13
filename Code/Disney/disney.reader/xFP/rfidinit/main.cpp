/**
    @file   main.cpp - Main file for rfidinit program
    @author Greg Strange
    @date   September 2012


    The rfidinit program serves the single purpose of writing the necessary keys
    to the SAM chip of the RFID radio circuitry.  These keys are encrypted and
    embedded in this program.  Because the keys are contained in this program,
    this program should never be left on a device or widely distributed. Instead,
    it should be run once during the manufacturing process and then deleted.

    In case a bad guy does get a hold of this program, the keys are encrypted to
    make it a little tougher to extract them.  While this would certainly not
    stop a determined hacker, at least the keys will not show up in a "strings"
    search.

    Keys are embedded in the rfidinit program as follows

    1) Keys are defined in json format in keys.json
    2) keys.json is encrypted by the makefile into keys.enc
    3) keys.enc is turned into a byte array by "xxd" and output to keys.h

    The program decrypts the keys in keys.h, then parses the keys out of the
    json and writes them to the SAM.
*/


#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <string>

#include "standard.h"
#include "log.h"
#include "SynapseReader.h"
#include "RFIDExceptions.h"
#include "RFIDTag.h"
#include "Desfire.h"
#include "UltralightC.h"
#include "ticks.h"
#include "keys.h"
#include "AES128Key.h"

#ifndef _WIN32
#include <unistd.h>
#include <termios.h>
#include <fcntl.h>
#include <signal.h>
#endif

#include "json/json.h"

#include <iostream>
#include <fstream>

using namespace Reader;
using namespace RFID;


#ifndef _WIN32
#define LOG_FILE_PATH   "/var/tmp/rfidinit.log"
#else
#define LOG_FILE_PATH   "rfidinit.log"
#define FULL_VERSION    "unknown"
#endif



static SynapseReader _reader;





static void provisionSam()
{
    // Create the key to be used to decrypt the keys to write to the SAM
	uint8_t keyBytes[] = { 0x3a, 0x40, 0xb8, 0xc1, 0x2d, 0xef, 0x09, 0xa8, 0x74, 0xce, 0xfd, 0x12, 0x09, 0x33, 0x3a, 0x54 };
    AES128Key key(keyBytes, sizeof(keyBytes));

    // Decrypt the keys to write to the SAM
    // The keys are now in json format
    ByteArray keysJsonText = key.decrypt(keys_enc, sizeof(keys_enc));

    // Parse the json formatted keys to write to the SAM
    Json::Reader jsonReader;
    Json::Value values;
    if (!jsonReader.parse((char*)keysJsonText.data(), (char*)keysJsonText.data() + keysJsonText.size(), values, false))
        LOG_WARN("unable to parse keys\n");

    // Write the keys
    _reader.provisionSam(values);
}




void usage()
{
    printf("\n");
    printf("usage:\n");
    printf("    rfidinit [options]\n");
    printf("\n");
    printf("options:\n");
    printf("    -v                 print version and exit\n");
}



int main(int argc, char** argv) 
{
    printf("rfidinit "FULL_VERSION"\n");

    // parse command line arguements
    for (int i = 1; i < argc; ++i)
    {
        char* arg = argv[i];

        if (*arg == '-')
        {
            ++arg;
            switch (*arg)
            {
            case 'v':
                exit(0);

            default:
                usage();
                exit(0);
            }
        }
    }

    // Open log file
    logInit(NULL, "RFID ver "FULL_VERSION"\n");

    // For debugging, you can set the log level to LOG_LEVEL_VERBOSE, but do not leave it
    // that way in production because the verbose log messages will include messages to
    // the SAMs with the keys.
    logSetConsoleLevel(LOG_LEVEL_INFO);

    std::string errorMsg;
    if (!_reader.init(errorMsg, false))
        LOG_ERROR("reader init failed because: %s", errorMsg.c_str());
    else
    {
        LOG_INFO("reader init succeeded\n");


        try
        {
            provisionSam();
        }
        catch (RFIDException e)
        {
            LOG_DEBUG("Caught RFID exception '%s'", e.what());
        }
        catch (...)
        {
            LOG_DEBUG("caught something\n");
        }
    }

    logClose();
}
