#
# make.inc
# August 29, 2011
# Greg Strange
#
# Some make stuff common to all makefiles
#
# This sets up the tool names and paths.  If TARGET=omap is selected, then the Omap
# cross compiler tools will be selected.
#
# If using devshell, then set TARGET= anything but omap.  The tool names set by devshell
# will then take precedence over the tool names set up at the end of this file.
#
TARGET?=omap
#TARGET?=friendlier



ifeq ("$(TARGET)", "omap")

ifndef ARAGO
$(error Need definition for ARAGO (i.e. directory bitbake was run from))
endif

# Location of ARM cross compiler tools
TOOLPATH:=/opt/arm-2009q1

# Tool names
AR:=arm-none-linux-gnueabi-ar
CC:=arm-none-linux-gnueabi-gcc -march=armv7-a -mtune=cortex-a8 -mfpu=neon -mfloat-abi=softfp -mthumb-interwork -mno-thumb --sysroot=$(ARAGO)/arago-tmp/sysroots/armv7a-none-linux-gnueabi
CCLD:=arm-none-linux-gnueabi-gcc -march=armv7-a -mtune=cortex-a8 -mfpu=neon -mfloat-abi=softfp -mthumb-interwork -mno-thumb --sysroot=$(ARAGO)/arago-tmp/sysroots/armv7a-none-linux-gnueabi
CXX:=arm-none-linux-gnueabi-g++ -march=armv7-a -mtune=cortex-a8 -mfpu=neon -mfloat-abi=softfp -mthumb-interwork -mno-thumb --sysroot=$(ARAGO)/arago-tmp/sysroots/armv7a-none-linux-gnueabi

# Add the tool directories to the path
PATH:=$(ARAGO)/arago/bin:$(ARAGO)/arago-oe-dev/bin:$(ARAGO)/arago-tmp/sysroots/x86_64-linux/usr/armv7a/bin:$(ARAGO)/arago-tmp/sysroots/x86_64-linux/usr/sbin:$(ARAGO)/arago-tmp/sysroots/x86_64-linux/usr/bin:$(ARAGO)/arago-tmp/sysroots/x86_64-linux/sbin:$(ARAGO)/arago-tmp/sysroots/x86_64-linux/bin:$(TOOLPATH)/bin/:$(ARAGO)/arago-bitbake/bin/:$(ARAGO)/arago-utils/:$(PATH)

else

AR?=ar
CXX?=$(CROSS_COMPILE)g++
CC?=$(CROSS_COMPILE)gcc

endif



