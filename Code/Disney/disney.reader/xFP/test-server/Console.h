#ifndef __CONSOLE_H
#define __CONSOLE_H



#include <stdio.h>




class Console
{
public:
    static Console* instance();

    void close();
    bool getLine(char* returnBuf, size_t bufLength);
    void printf(const char* format, ...);
    void print(const char* s);

    
private:    
    // singleton, so keep constructors and destructors private
    Console();
    ~Console();
    Console(const Console&);
    const Console& operator=(const Console&);
};



extern Console console;


#endif
