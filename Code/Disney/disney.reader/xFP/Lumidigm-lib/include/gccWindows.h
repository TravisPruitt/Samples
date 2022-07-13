#include <unistd.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
long GetTickCount(void);
char getch(void);
char _getch(void);
char _putch(int ch);
#ifndef BOOL
typedef int BOOL;
#endif
#ifndef DWORD
typedef unsigned long DWORD;
#endif
void Sleep(unsigned int seconds);
#define FALSE 0
#define TRUE
