#ifndef __SysGpio_H__
#define __SysGpio_H__

#include <string>
#include "gpiobank.h"

namespace Reader
{
	class SysGpio : public GpioBank
	{
		public:
			SysGpio();
			virtual ~SysGpio();
		public:
			virtual bool get(unsigned pin);
			virtual bool set(unsigned pin, bool value);
			virtual bool output(unsigned pin, bool value);
			virtual bool input(unsigned pin);
			virtual bool is_input(unsigned pin);
			virtual bool test();
			virtual bool init();
		private:
			bool exportGpio(unsigned gpio);
			bool writeGpio(const char* what,
				       const char* file,
				       unsigned gpio);
			std::string readGpio(const char* file,
					     unsigned gpio);
	};
};

#endif //  __SysGpio_H__
