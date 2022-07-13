/**
    @file ssconsole.cpp
    @Author Greg Strange

    Console class.  There are two reasons to use this class.

    1) It handles diffrences between Windows and Linux
    2) It logs everything written to the console to the log file
*/



#include <stdio.h>
#include <stdarg.h>
#include "standard.h"
#include "Console.h"



#ifdef _WIN32

#include <conio.h>
#include <string.h>


#else

#include <sys/types.h>
#include <sys/time.h>
#include <unistd.h>
#include <string.h>
#include <ctype.h>

#include <termios.h>


#endif


const char CTRL_A = ('A' & 0x3f);
const char CTRL_Z = ('Z' & 0x3f);


Console* Console::instance()
{
    static Console console;
    return &console;
}


/**
    Constructor
*/
Console::Console()
{
}


/**
    Destructor
*/
Console::~Console()
{
}


#ifdef _WIN32

static char buf[500];
static int16_t index = 0;

/**
    Get the next complete line from the console if there is one.
  
    @param    returnBuf   Buffer to put line in
    @param    bufLength   Size of the buffer
  
    @return   true if there is a line, false if not
    
    If the text line is too long for the buffer, it is truncated.  
    The ending carraige return or new line is NOT included in the returned text.
    The returned text will always end with a '\0'.
    
    If the user enters a control character, this routine returns true and the
    buffer holds just the control character.
*/
bool Console::getLine(char* returnBuf, size_t bufLength)
{
    if (_kbhit())
    {
        char c = (char)_getch();
        // End of the line (carriage return or new line)
        if (c == '\n' || c == '\r')
        {
            buf[index] = 0;
            index = 0;
            _putch('\r');
            _putch('\n');
            strncpy(returnBuf, buf, bufLength-1);
            returnBuf[bufLength-1] = 0;
            return true;
        }
        // Backspace
        else if (c == '\b')
        {
            if (index > 0)
            {
                _putch('\b');
                _putch(' ');
                _putch('\b');
                --index;
            }
        }
        else if (c >= CTRL_A && c <= CTRL_Z)
        {
            returnBuf[0] = c;
            returnBuf[1] = 0;
            return true;
        }
        // Anything else gets stuffed in the buffer
        else if (index < (sizeof(buf)/sizeof(buf[0]) - 1) )
        {
            // At this point tabs are converted to spaces.  We can implement tabs later
            // if we find a use for them, but we will have to change the backspace code above 
            // if we do.
            if (c == '\t')    c = ' ';
            buf[index++] = c;
            _putch(c);
        }
    }
    return false;
}


#else   // POSIX version




/**
    Is a line of text available from the console?
    
    @return true if a line of text is available, false if not.
*/
static bool lineReady()
{
    int sel_ret_val;
    int s_set = STDIN_FILENO + 1;

    fd_set  keyboard;
    struct timeval timeout;

    timeout.tv_sec = 0;  /* return immediately */
    timeout.tv_usec = 0;

    FD_ZERO( &keyboard );
    FD_SET( STDIN_FILENO, &keyboard );

    if( (sel_ret_val = select(s_set, &keyboard, NULL, NULL, &timeout)) < 0 )
        return false;

    if( FD_ISSET(STDIN_FILENO, &keyboard) )
        return true;

    return false;
}



/*
    Get the next complete line from the console if there is one.
    
    @param returnBuf    Buffer to put line in
    @param bufLength    size of the buffer
    
    @return true if a line was read, false if not
    
    If the text line is too long for the buffer, it is truncated.  
    The ending carraige return or new line is NOT included in the returned text.
    The returned text will always end with a '\0'.
*/
bool Console::getLine(char* returnBuf, size_t bufLength)
{
    if (lineReady())
    {
        fgets(returnBuf, bufLength, stdin);
        size_t len = strlen(returnBuf);
        if ( (len > 0) && (returnBuf[len-1] == '\n' || returnBuf[len-1] == '\r') )
        {
            returnBuf[len-1] = 0;     // chop off ending carraige return or line feed
        }
        return true;
    }
    else
        return false;
}


#endif






/**
    Printf to the console (and the log file)
*/
void Console::printf(const char* format, ...)
{
    char buf[2000];

    va_list args;
    va_start(args, format);
    
    vsnprintf(buf, sizeof(buf)-1, format, args);
    buf[sizeof(buf)-1] = 0;     // guarantee a null terminator

    print(buf);
}



/**
    Print some text to the console (and the log file)
*/
void Console::print(const char* s)
{
    fputs(s, stdout);
}
