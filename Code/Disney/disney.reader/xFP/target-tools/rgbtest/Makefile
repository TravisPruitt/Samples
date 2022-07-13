OUT_DIR:=../../out
ARAGO:=../../..

include ../../../common/make.inc

CFLAGS ?= -W -Wall -g
CFLAGS+=-D_DEBUG
CFLAGS+=-DJSON_IS_AMALGAMATION
CFLAGS+=-DNO_DAP_CONFIG

RGBTEST := $(OUT_DIR)/rgbtest

.PHONY: clean

DAP_SRC:=../../reader/Thread.cpp ../../reader/I2C.cpp \
	../../reader/Event.cpp ../../reader/WavFile.cpp

$(RGBTEST): rgbtest.cpp tlc59116.cpp rgbled.cpp rgbcluster.cpp rgbcolor.cpp LedScript.cpp ColorEffect.cpp log.cpp Sound.cpp $(DAP_SRC)
	$(CXX) $(CFLAGS) $(LDFLAGS) -I. -I../../reader -lasound -o $@ $^

clean:
	rm -f $(OUT_DIR)/rgbtest
