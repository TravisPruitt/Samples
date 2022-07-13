#
# Makefile
#	grover project
#		radio_driver	- linux kernel module to communicate with SPI interface to Radios
#		radiotool		- debugging tool for radio_driver
#		radioflasher	- utility application to write software updates to the radios.
#
#	copyright Synapse Product Development	info@synapse.com
# 	2011 - 2012		
#

ARAGO:=../..
include ../../common/make.inc

obj-m	:= grover_radio.o

grover_radio-y := grover_radio_main.o grover_radio_tx.o grover_radio_debug.o grover_radio_loadgen.o grover_radio_sysfs.o

PWD       := $(shell pwd)
KERNELDIR ?= $(PWD)/../../arago-tmp/sysroots/overo-none-linux-gnueabi/kernel
ARCH	  ?= arm
CROSS_COMPILE ?= arm-none-linux-gnueabi-
CC        = $(CROSS_COMPILE)gcc
CXX	  = $(CROSS_COMPILE)g++

#######################
# Create the Application version
include version.inc
include ../../common/version-string.inc
CVERSION=-DVERSION="\"$(FULL_VERSION)\""
####################

export ARCH CROSS_COMPILE

all: grover_radio.ko radiotool radioflasher

grover_radio_version.h:
	echo "#define VERSION \"$(FULL_VERSION)\"" > grover_radio_version.h

# added --include... - this causes the kernel to see the "include version.inc", above.
grover_radio.ko: grover_radio_main.c grover_radio.h grover_radio_tx.c Makefile \
	grover_radio_main.h grover_radio_debug.c grover_radio_version.h \
	grover_radio_loadgen.c grover_radio_loadgen.h grover_radio_version.h \
	grover_radio_sysfs.c
	$(MAKE) -C $(KERNELDIR) M=$(PWD) --include-dir=$(PWD)
	rm -f grover_radio_version.h

radiotool: radiotool.c grover_radio.h radiocommands.h
	$(CXX) $(CVERSION) -W -Wall -o $@ $< -Wl,-O1 -Wl,--hash-style=gnu

radioflasher: radioflasher.cpp ihex.cpp
	$(CXX) $(CVERSION) -W -Wall -o $@ $^ -Wl,-O1 -Wl,--hash-style=gnu

modules_install:
	$(MAKE) -C $(KERNELDIR) M=$(PWD) INSTALL_MOD_PATH=$(INSTALL_MOD_PATH)  modules_install

clean:
	rm -rf *.o *~ core .depend .*.cmd *.mod.c .tmp_versions modules.order Module.symvers radioflasher radiotool

distclean: clean
	rm -rf radiotool radioflasher *.ko
