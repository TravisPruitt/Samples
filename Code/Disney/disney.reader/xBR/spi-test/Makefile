#
# Makefile
#	grover project
#		spi-test	- target application to test reliability of SPI
#							communications.
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

CROSS_COMPILE ?= arm-none-linux-gnueabi-
CC        = $(CROSS_COMPILE)gcc
CXX       = $(CROSS_COMPILE)g++

OUT_DIR=../out

CFLAGS = -W -Wall -I../radio_driver
TARGETS = $(OUT_DIR)/spi-test $(OUT_DIR)/mfg-spi-test

LDFLAGS ?=

all: ${TARGETS}

$(OUT_DIR)/spi-test : main.c groverlib.c random.c
	@ mkdir -p $(OUT_DIR)
	${CXX} ${CFLAGS} $(CVERSION) $(LDFLAGS) -o $@ $^

$(OUT_DIR)/mfg-spi-test: mfg-spi-test
	install -m 755 $^ $@

clean:
	rm -f ${TARGET} *.o *~ ${TARGETS}
