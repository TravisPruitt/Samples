#include <sys/types.h>
#include <sys/stat.h>
#include <fcntl.h>
#include <cstdlib>
#include <string.h>
#include <stdio.h>
#include <errno.h>
#include <unistd.h>
#include <limits.h>


#include "sysgpio.h"

namespace Reader
{
	SysGpio::SysGpio()
	{
	}

	SysGpio::~SysGpio()
	{
	}


	bool SysGpio::get(unsigned pin)
	{
		std::string val = 
			readGpio("value", pin);
		int v = atoi(val.c_str());
		return v == 0 ? false : true;
	}

	bool SysGpio::set(unsigned pin, bool value)
	{
		return writeGpio(value ? "1" : "0", "value", pin);
	}

	bool SysGpio::output(unsigned pin, bool value)
	{
		writeGpio("out", "direction", pin);
		return set(pin, value);
	}

	bool SysGpio::input(unsigned pin)
	{
		return writeGpio("in", "direction", pin);
	}

	bool SysGpio::is_input(unsigned pin)
	{
		std::string val = 
			readGpio("direction", pin);
		return val == "input" ? true : false;
	}

	bool SysGpio::exportGpio(unsigned gpio)
	{
		char path[PATH_MAX];
		char val[16];
		int fd;

		snprintf(path, sizeof(path), "/sys/class/gpio/export");

		fd = ::open(path, O_WRONLY);
		if(fd < 0)
			return false;

		snprintf(val, sizeof(val), "%u\n", gpio);
		int ret = ::write(fd, val, strlen(val));
		close(fd);

		return ret > 0 ? true : false;
	}

	bool SysGpio::init()
	{
		exportGpio(74);
		return true;
	}

	bool SysGpio::writeGpio(const char *what,
			       	const char* file,
			       	unsigned gpio)
	{
		char path[PATH_MAX];
		int ret;

		snprintf(path, sizeof(path),
			 "/sys/class/gpio/gpio%u/%s",
			 gpio, file);

		int fd = ::open(path, O_WRONLY);
		if(fd < 0)
			return false;

		ret = ::write(fd, what, strlen(what));
		close(fd);

		return true;
	}

	std::string SysGpio::readGpio(const char* file,
				      unsigned gpio)
	{
		char path[PATH_MAX];
		char data[16];
		int ret;

		snprintf(path, sizeof(path),
			 "/sys/class/gpio/gpio%u/%s",
			 gpio, file);

		int fd = ::open(path, O_RDONLY);
		if(fd < 0)
			return "";

		memset(data, 0, sizeof(data));
		ret = ::read(fd, data, sizeof(data) - 1);
		close(fd);

		return std::string(data);
	}

	bool SysGpio::test()
	{
		struct stat s;
		int ret;

		ret = stat("/sys/class/gpio/export", &s);

		return ret == 0 ? true : false;
	}
};
