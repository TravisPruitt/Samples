#
# Makefile
#	grover project
#		cli-over-spi	- Test and debugging interface with Radios 
#				  over the SPI bus. Very low level support.
#
#	copyright Synapse Product Development	info@synapse.com
# 	2011 - 2012		
#

ARAGO:=../..
include ../../common/make.inc

#######################
# Create the Application version
include version.inc
include ../../common/version-string.inc
CVERSION=-DVERSION="\"$(FULL_VERSION)\""
####################

#CROSS_COMPILE ?= arm-none-linux-gnueabi-
#CC        = $(CROSS_COMPILE)gcc
#CXX	  = $(CROSS_COMPILE)g++

OUT_DIR=../out

CFLAGS = -W -Wall -I../radio_driver 
TARGET = $(OUT_DIR)/radio-cli

LDFLAGS ?=

${TARGET} : main.c monitor.c strings.c character.c radio.c time.c groverlib.c
	@ mkdir -p $(OUT_DIR)
	${CXX} ${CFLAGS}  -I../radio_driver $(CVERSION) -o $@ $^ -lrt ${LDFLAGS}

clean:
	rm -f ${TARGET} *.o *~
