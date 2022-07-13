#include "gccWindows.h"
#include <sys/times.h>
//#include <unistd.h>
#include <stdio.h>
#include <time.h>
long GetTickCount(void)
{
	return(time(0)*1000);
}

void Sleep(unsigned int seconds)
{
	tms tm;
	unsigned long starttime,elapsedtime;
	//printf("sleeping for %d seconds\n",seconds);
	starttime = times(&tm);
	do{
		elapsedtime = times(&tm) - starttime;
	} while(elapsedtime < seconds*100);
	
}

char getch(void)
{
	return(getchar());
}

char _getch(void)
{
	return(getchar());
}


char _putch(int ch)
{
	return(putchar(ch));
}
