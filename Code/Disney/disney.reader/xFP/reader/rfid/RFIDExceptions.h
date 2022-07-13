/**
    @file   RFIDExceptions.h
    @author Roy Sprowl
    @date   Jan 2012

    Copyright (c) 2012, synapse.com
*/

#ifndef __RFID_EXCEPTIONS_H
#define __RFID_EXCEPTIONS_H

//#include <exception>
//#include <stdexcept>
#include <string>
#include <stdarg.h>

namespace RFID
{

#if 0
// InitializationExceptions are thrown when the application cannot successfully start
class InitializationException : public std::runtime_error
{
public:
    InitializationException(const char* szError) : std::runtime_error(szError) {}
};
#endif

// Base class for all errors after initialization
class RFIDException
{
public:
    const char* what() { return buf; }

private:
    char buf[1000];

protected:
    RFIDException() { buf[0] = 0; }

    RFIDException(const char* format, ...)
    {
        va_list args;
        va_start(args, format);
        init(format, args);
    }

    void init(const char* format, va_list& args)
    {
#ifndef _WIN32
        vsnprintf(buf, sizeof(buf), format, args);
#else
        // A sanity check to protect against buffer overflows, but of course
        // we don't know how big the args are, so its still a crap shoot.
        if (strlen(format) < sizeof(buf)-50)
            vsprintf(buf, format, args);
        else
        {
            strncpy(buf, format, sizeof(buf) - 1);
            buf[sizeof(buf)-1] = 0;
        }
#endif
    }
};

#if 0
// CommExceptions are thrown when the application fails to read or write from the COM port.
// If this happens in the embedded system, a reboot is probably needed.
class CommException : public RFIDException
{
public:
    CommException(const char* szError) : RFIDException(szError) {}
};

// StatusExceptions mean that something went wrong with an Inventory or other PICC read.
// This may be caused by a program bug or by premature removal of the PICC
class StatusException : public RFIDException
{
public:
    StatusException(const char* szError) : RFIDException(szError) {}
};
#endif

// BugExceptions mean that code is doing something unexpected.
class BugException : public RFIDException
{
public:
    BugException(const char* format, ...)
    {
        va_list args;
        va_start(args, format);
        init(format, args);
    }
};


/**
    Exception with communciations with the RFID tag.

    In general, these do not indicate a hardware or software problem, but usually only
    indicate a problem with the RFID tag, or simply a tag that is not within range for
    reliable communications.
*/
class TagException : public RFIDException
{
public:
    TagException() : RFIDException("RFID tag exception") {};
    TagException(const char* format, ...)
    {
        va_list args;
        va_start(args, format);
        init(format, args);
    }
};

/**
    Exception spawned in communications with the SAM card
*/
class SAMException : public RFIDException
{
public:
    SAMException() : RFIDException("SAM exception") {};
    SAMException(const char* format, ...)
    {
        va_list args;
        va_start(args, format);
        init(format, args);
    }
};


/**
    Exception spawned by communications with the TDA8029
*/
class ReaderException : public RFIDException
{
public:
    ReaderException() : RFIDException("RFID reader exception") {};
    ReaderException(const std::string& what) : RFIDException(what.c_str()) {};
    ReaderException(const char* format, ...)
    {
        va_list args;
        va_start(args, format);
        init(format, args);
    }
};

}

#endif
