#include <iostream>
#include <stdint.h>
#include <signal.h>
#include <cstring>
#include <cstdlib>
#include "rgbcluster.h"
#include "LedScript.h"
#include "ColorEffect.h"
#include "Sound.h"
#include "log.h"


using namespace Reader;
using namespace std;

#define DEFAULT_BRIGHTNESS 0xC0

void usage()
{
	cout << "rgbtest [options] <command>|<led> <r> <g> <b>\n";
	cout << "led	- number 0-47 or 'all'\n\n";
	cout << "rgbtest script <filename>\n";
	cout << "	  run script from file\n\n";
	cout << "rgbtest <led> <color>\n";
	cout << "led	- number 0-47 or 'all'\n";
	cout << "color	- red, blue, green etc...\n\n";
	cout << "rgbtest cycle\n";
	cout << "rgbtest reset\n";
	cout << "Options\n";
    cout << "\t-b <1-255>\tSet brightness level\n";
	cout << "\t-a\t\tAntenna board installed (Deprecated)\n";
}


void cycle(RGBCluster *cluster)
{
	RGBColor testcolors[3] = {
		RGBColor(255, 0, 0), // red
		RGBColor(0, 255, 0), // green
		RGBColor(0, 0, 255), // blue
	};

	unsigned prev = 0;
	for(unsigned c = 0; c < 3; c++)
	{
		for(unsigned i = 0; i < 48; i++) {
			cluster->setColor(prev, RGBColor(0, 0, 0));
			cluster->setColor(i, testcolors[c]);
			cluster->update();
			usleep(50000);
			prev = i;
		}
	}
    
    cluster->clearAll();
    cluster->update();
}


static RGBCluster *cluster = NULL;


void signal_handler(int /* signal */)
{
    cout << "\nTerminating..." << endl;
    if (cluster)
    {
        cluster->clearAll();
        cluster->update();
        delete cluster;
    }
	exit(0);
}


int main(int argc, char* argv[])
{
	unsigned led, r, g, b;
	int c;
	bool all = false;
	bool have_antenna_board = false;
	bool need_color = false;
    unsigned brightness = DEFAULT_BRIGHTNESS;

	while ((c = getopt(argc, argv, "ab:")) != -1) {
		switch (c) {
			case 'a':
				have_antenna_board = true;
				break;
			case 'b':
				brightness = strtoul(optarg, 0, 0);
				if (brightness > 0xFF || brightness == 0) {
					cout << "Warning: Invalid brightness level, ingnoring..." << endl;
					brightness = DEFAULT_BRIGHTNESS;
				}
                break;
		}
	}

	if((argc - optind) < 1) {
		usage();
		return 0;
	}

	signal(SIGINT, signal_handler);

	cluster = new RGBCluster();
	cluster->init(true, 0x6F);
    cluster->setGlobalBrightness(brightness);

	Sound::instance()->start();

	logSetLogLevel(LOG_LEVEL_DEBUG);

	if(strcmp(argv[optind], "all") == 0) {
		all = true;
		need_color = true;
	} else if(strcmp(argv[optind], "script") == 0) {
		LedScript script(cluster);
		script.loadFile(argv[optind+1]);
		int waitTime = 0;
		while(waitTime >= 0)
		{
			if (waitTime > 0)
				usleep(waitTime*1000);
			waitTime = script.next();
		}
		return 0;
	} else if(strcmp(argv[optind], "cycle") == 0) {
		cycle(cluster);
		return 0;
	} else if(strcmp(argv[optind], "reset") == 0) {
		return 0; // cluster->init resets chips so just return
	} else {
		led = strtoul(argv[optind], 0, 0);
		need_color = true;
	}

	ColorEffect color(cluster);
	if(need_color) {
		optind++;
		if(argc == (optind + 1))
		{
			if (!color.setColorEffect(argv[optind]))
			{
				cout << "Error: Invalid color effect" << endl;
				return 0;
			}
		}
        else if(argc == (optind + 3))
        {
            r = strtoul(argv[optind], 0, 0);
            g = strtoul(argv[optind+1], 0, 0);
            b = strtoul(argv[optind+2], 0, 0);
            color.setColor(r, g, b);
        }
		else
		{
			usage();
			return 0;
		}
	}
	color.next();
	delete cluster;
}
