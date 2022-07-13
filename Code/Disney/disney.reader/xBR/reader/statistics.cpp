//*****************************************************************************
// manager.cpp 
//
//	xBand Reader: oversee the reader state
//*****************************************************************************
//
//	Written by Mike Wilson 
//	Copyright 2011, Synapse.com
//
//*****************************************************************************
//

// C/C++ Std Lib
#include <string>

// System Lib
#include "json/json.h"

// Global-App
#include "grover.h"
#include "logging.h"
#include "config.h"
// Local-ish
#include "sysIface.h"
#include "controller.h"
#include "events.h"
#include "radio.h"

namespace statistics
{

	static uint8_t timeslot = 0;
	static uint32_t poll[10];		// 10 seconds worth
	static uint32_t sss[8][10]; 	// and for 10 radios
	static uint32_t ssc[8][10]; 	// and for 10 radios
	static uint32_t sec = 0;

	namespace statsThread
	{
		timerThread::THS Ths = THS_INIT;

		// timer is configured to run a thread at timeout
		void init()
		{
			timerThread::init(&Ths, compute);
		}

		void start(time_t sec, long nsec)
		{
			timerThread::periodic(&Ths, sec, nsec);
		}
			
		void off()
		{
			timerThread::off(&Ths);
		}
		
	}

	void init()
	{
		uint8_t t, r;
		for(t=0;t<10;t++)
		{
			poll[t] = 0;
			for(r=0;r<8;r++)
			{	sss[r][t] = 0;
				ssc[r][t] = 0;
			}
		}
		
		statsThread::init();
	}

	void add(uint8_t r,uint8_t ss)
	{
		if(pthread_mutex_lock(&statsThread::Ths.mtx)) log::errr("manager unlock failed");
		{
			poll[timeslot]++;
			if(ss)
			{
				sss[r][timeslot] += ss;
				ssc[r][timeslot]++;
			}			 
		}
		if(pthread_mutex_unlock(&statsThread::Ths.mtx)) log::errr("manager unlock failed");
	}

	void second(union sigval sv)
	{	
		uint8_t t, r;
		float polls = 0;

		sec++;
		uint16_t c[8];
		uint16_t avg[8];

		if(pthread_mutex_lock(&statsThread::Ths.mtx)) log::errr("manager lock failed");
		{
			for(t=0;t<10;t++)
			{
				polls += poll[t];
			}

			for(r=0;r<8;r++)
			{
				c[r] = 0;
				avg[r] = 0;
				for(t=0;t<10;t++)
				{
					c[r] += ssc[r][t];
					avg[r] += sss[r][t];
				}
				avg[r]=(c[r]) ? avg[r] / c[r] : 0;
			}
		}
		if(pthread_mutex_unlock(&statsThread::Ths.mtx)) log::errr("manager unlock failed");
		
		log::stat("p:%2.1f ss:%2u-%2u %2u-%2u %2u-%2u %2u-%2u %2u-%2u %2u-%2u %2u-%2u %2u-%2u\n",
			polls / (8 * 10),
			c[0], avg[0], c[1], avg[1], c[2], avg[2], c[3], avg[3],	c[4], avg[4], c[5], avg[5], c[6], avg[6], c[7], avg[7]);

		if(pthread_mutex_lock(&statsThread::Ths.mtx)) log::errr("manager lock failed");
		{
			timeslot++;
			timeslot %= 10;

			poll[timeslot] = 0;
			for(r=0;r<8;r++)
			{	sss[r][timeslot] = 0;
				ssc[r][timeslot] = 0;
			}
		}
		if(pthread_mutex_unlock(&statsThread::Ths.mtx)) log::errr("manager unlock failed");

	}	
}

