#include <stdio.h>
#include <stdlib.h>
#include <string.h>

#include "standard.h"
#include "log.h"
#include "SynapseReader.h"
#include "RFIDExceptions.h"
#include "RFIDTag.h"
#include "Desfire.h"
#include "UltralightC.h"
//#include "version.h"
#include "ticks.h"

#ifndef _WIN32
#include <unistd.h>
#include <termios.h>
#include <fcntl.h>
#include <signal.h>
#endif

#include "json/json.h"

#include <iostream>
#include <fstream>
//#include <string>
//#include <algorithm>
//#include <fcntl.h>
//#include <errno.h>

using namespace Reader;
using namespace RFID;


#ifndef _WIN32
#define LOG_FILE_PATH   "/var/log/rfid.log"
#else
#define LOG_FILE_PATH   "rfid.log"
#define FULL_VERSION    "unknown"
#endif



static SynapseReader _reader;
static RFIDTag* _lastTag;
static MILLISECONDS _lastRFIDTime;




static bool kbhit()
{
#ifndef _WIN32
    struct termios oldt, newt;
   
    tcgetattr(STDIN_FILENO, &oldt);
    newt = oldt;
    newt.c_lflag &= ~(ICANON | ECHO);
    tcsetattr(STDIN_FILENO, TCSANOW, &newt);
    int oldflags = fcntl(STDIN_FILENO, F_GETFL, 0);
    fcntl(STDIN_FILENO, F_SETFL, oldflags | O_NONBLOCK);

    int ch = getchar();

    tcsetattr(STDIN_FILENO, TCSANOW, &oldt);
    fcntl(STDIN_FILENO, F_SETFL, oldflags);

    if (ch != EOF)
    {
        ungetc(ch, stdin);
        return true;
    }

    return false;
#else
    return _kbhit();
#endif
}


static void provisionSam(const char* fileName)
{
    if (fileName == NULL || strlen(fileName) <= 0)
    {
        LOG_WARN("no keys file name given\n");
        return;
    }

    std::ifstream infile;
    infile.open(fileName, std::ifstream::in);
    if (infile.fail())
    {
        LOG_WARN("unable to open keys file '%s'\n", fileName);
    }
    else
    {
        Json::Reader jsonReader;
        Json::Value values;
        if (!jsonReader.parse(infile, values, true))
            LOG_WARN("unable to parse keys file '%s'\n", fileName);

        infile.close();

        LOG_INFO("writing keys from '%s'\n", fileName);

        _reader.provisionSam(values);
    }
}



/**
    Read one tag (if present).
*/
static bool readTag(bool readPublicId, bool readSecureId)
{
    try
    {
        ByteArray uid;
        bool isDesfire;

        if (_reader.readTag(uid, isDesfire, 2000))
        {
            RFIDTag* tag;

            if (isDesfire)
                tag = new Desfire(uid);
            else
                tag = new UltralightC(uid);

            if (( (getMilliseconds() - _lastRFIDTime) < 500) && (strcmp(_lastTag->getUID(), tag->getUID()) == 0) )
            {
                _lastRFIDTime = getMilliseconds();
                LOG_INFO("ignoring duplicate tap");
            }
            else
            {
                LOG_INFO("Got %s tag", isDesfire ? "Desfire" : "Ultralight");
                bool success = false;

                try
                {
                    if (readPublicId)
                    {
                        tag->readIds(_reader.getSAM(), readSecureId);
                        LOG_INFO("    UID: %s", tag->getUID());
                        LOG_INFO("    PID: %s", tag->getPublicId());
                        if (readSecureId && tag->hasSecureId())
                        {
                            LOG_INFO("    IIN: %s", tag->getIIN());
                            LOG_INFO("    SID: %s", tag->getSecureId());
                        }
                    }
                    else
                        LOG_INFO("    UID: %s", tag->getUID());

                    success = ( (tag->hasPublicId() || !readPublicId) && (tag->hasSecureId() || !readSecureId) );
                }
                catch (TagException ex)
                {
                    LOG_INFO("ignoring Tag exception");
                }

                if (success)
                {
                    _lastTag = tag;
                    _lastRFIDTime = getMilliseconds();
                }
                // If a desfire card fails to read, then we have to cycle power on the radio before we can retry.
                else if (isDesfire)
                {
                    _reader.getSAM()->stopRadio();
                    _reader.getSAM()->startRadio();
                }
            }

            return true;
        }
    }
    catch (RFIDException e)
    {
        LOG_DEBUG("Caught RFID exception '%s'", e.what());
    }

    return false;
}



bool parseRegisterFile(const char* fileName, ByteArray& registers, ByteArray& values)
{
    FILE* fp = fopen(fileName, "r");
    if (fp == NULL)
    {
        printf("Unable to open file '%s'\n", fileName);
        return false;
    }

    char buf[512];
    unsigned lineNumber = 0;
    bool error = false;
    while (!error && fgets(buf, sizeof(buf), fp))
    {
        ++lineNumber;

        char* s = buf;
        while (isspace(*s))    ++s;
        if (StringLib::ishex(*s))
        {
            uint8_t address;
            uint8_t value;
            if (StringLib::hex2bin(s, &address, 2))
            {
                while (StringLib::ishex(*s))    ++s;
                while (*s && !StringLib::ishex(*s))    ++s;
                if (StringLib::hex2bin(s, &value, 2))
                {
                    registers.append(address);
                    values.append(value);
                }
                else
                    error = true;
            }
            else
                error = true;
        }
    }

    fclose(fp);

    if (error)
    {
        printf("Error at line %d of '%s'\n", lineNumber, fileName);
        return false;
    }

    if (registers.size() <= 0)
    {
        printf("No register address/value pairs found in file '%s'\n", fileName);
        return false;
    }

    printf("registers = %s\n", StringLib::formatBytes(registers.data(), registers.size()).c_str());
    printf("values    = %s\n", StringLib::formatBytes(values.data(), values.size()).c_str());

    return true;
}






void usage()
{
    printf("\n");
    printf("usage:\n");
    printf("    rfid [options]\n");
    printf("\n");
    printf("options:\n");
    printf("    -a                 Authenticate SAM using key #0 and report on success or failure\n");
    printf("    -k                 Dump information about keys stored on SAM\n");
    printf("    -l                 Loop until any key press.  Continuously read cards (default if no other options given)\n");
    printf("    -p                 Read public ID and UID only (do not read secure ID)\n");
    printf("    -r                 Read once and exit after reading one tag (or timeout)\n");
    printf("    -R [<addresses>]   Read radio registers\n");
    printf("                         Will read the addresses from -W command if no addresses given\n");
    printf("    -s                 Turn off the radio (stop radio)\n");
    printf("    -u                 Read UID only (do not read public ID or secure ID)\n");
    printf("    -v                 Report version and exit\n");
    printf("    -V                 Verbose mode\n");
    printf("    -W <file>          Write radio registers.\n");
    printf("                         <file> holds address/value pairs, one per line like this \"1a 20\"\n");
    printf("\n");
    printf("    --write-keys <key file>  Write keys from keys.json to the SAM\n");
    printf("\n");
    printf("examples:\n\n");
    printf("  rfidtest -R 0 5 1a\n\n");
    printf("    Displays the contents of radio registers 0, 5 and 1a\n\n");
    printf("  rfidtest -W values.txt -R -r\n\n");
    printf("    Writes radio registers from values.txt, then reads same registers,\n");
    printf("    and then tries to read a card\n\n");
    printf("  rfidtest -write-keys keys.json\n\n");
    printf("    Write keys from file 'keys.json' to the SAM\n\n");
}



int main(int argc, char** argv) 
{
    bool authenticateSam = false;
    bool dumpKeys = false;
    bool readSecureId = true;
    bool readPublicId = true;
    bool provision = false;
    bool readOnce = false;
    bool readLoop = false;
    bool verbose = false;
    bool writeRegisters = false;
    bool readRegisters = false;
    bool stopRadio = false;
    ByteArray writeAddresses;
    ByteArray writeValues;
    ByteArray readAddresses;
    const char* keysFileName = NULL;

    printf("rfid version "FULL_VERSION"\n");

    // parse command line arguements
    for (int i = 1; i < argc; ++i)
    {
        char* arg = argv[i];

        if (*arg == '-')
        {
            ++arg;
            switch (*arg)
            {
            case 'a':
                authenticateSam = true;
                break;

            case 'k':
                dumpKeys = true;
                break;

            case 'l':
                readLoop = true;
                break;

            case 'p':
                readSecureId = false;
                break;

            case 'r':
                readOnce = true;
                break;

            case 'R':
                uint8_t address;
                while ( (i+1 < argc) && (StringLib::hex2bin(argv[i+1], &address, 2)) )
                {
                    ++i;
                    readAddresses.append(address);
                }
                readRegisters = true;
                break;

            case 's':
                stopRadio = true;
                break;

            case 'u':
                readPublicId = false;
                readSecureId = false;
                break;

            case 'v':
                exit(0);

            case 'V':
                verbose = true;
                break;

            case 'W':
                if (++i >= argc)
                {
                    printf("Need register file name\n");
                    exit(1);
                }
                if (!parseRegisterFile(argv[i], writeAddresses, writeValues))
                    exit(1);
                writeRegisters = true;
                break;

            case '-':
                ++arg;
                if (strcmp(arg, "write-keys") == 0)
                {
                    if (++i >= argc)
                    {
                        printf("need keys file name\n");
                        exit(1);
                    }
                    keysFileName = argv[i];
                    provision = true;
                }
                else
                {
                    usage();
                    exit(0);
                }
                break;

            default:
                usage();
                exit(0);
            }
        }
    }

    if (readRegisters && (readAddresses.size() == 0) && (writeAddresses.size() == 0) )
    {
        printf("No register addresses given to read from\n");
        exit(1);
    }

    // Read loop is the default if we aren't doing one of these other items
    if (!readOnce && !provision && !dumpKeys && !authenticateSam && !readRegisters && !writeRegisters && !stopRadio)
        readLoop = true;

    // We only initialize the radio if we need it
    bool usingRadio = readOnce ||
                      readLoop ||
                      readRegisters ||
                      writeRegisters ||
                      stopRadio;

    // Open log file
    logInit(LOG_FILE_PATH, "RFID ver "FULL_VERSION"\n");
    logSetLogLevel(LOG_LEVEL_TRAFFIC);
    logSetConsoleLevel(verbose ? LOG_LEVEL_TRAFFIC : LOG_LEVEL_INFO);


    std::string errorMsg;

    bool initFailed = false;
    if (!_reader.init(errorMsg, usingRadio))
    {
        LOG_ERROR("reader init failed because: %s", errorMsg.c_str());
        LOG_INFO("reader init, second try");
        if (!_reader.init(errorMsg, usingRadio))
        {
            LOG_ERROR("reader init failed because: %s", errorMsg.c_str());
            initFailed = true;
        }
    }

    if (!initFailed)
    {
        LOG_INFO("reader init succeeded\n");

        try
        {
            if (authenticateSam)
            {
                if (_reader.getSAM()->authenticate())
                    LOG_INFO("SAM authentication passed");
                else
                    LOG_INFO("SAM authentication failed");
            }

            if (dumpKeys)
                _reader.dumpKeys();

            if (provision)
                provisionSam(keysFileName);

            if (writeRegisters)
                _reader.getSAM()->writeRadioRegisters(writeAddresses, writeValues);

            if (readRegisters)
            {
                ByteArray values;
                ByteArray* addresses = readAddresses.size() > 0 ? &readAddresses : &writeAddresses;
                _reader.getSAM()->readRadioRegisters(*addresses, values);
                for (unsigned i = 0; i < addresses->size(); ++i)
                    LOG_INFO("  %02x = %02x", (*addresses)[i], values[i]);
            }

            if (readOnce)
            {
                if (!readTag(readPublicId, readSecureId))
                {
                    LOG_INFO("No tag");
                }
            }

            if (readLoop)
            {
                _lastRFIDTime = 0;
                LOG_INFO("Hit any key to exit");
                while (!kbhit())
                {
                    readTag(readPublicId, readSecureId);
                }
            }

            if (usingRadio)
            {
                _reader.getSAM()->stopRadio();
                LOG_INFO("reader turned off");
            }
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
