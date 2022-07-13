OUT_DIR:=../../out
ARAGO:=../../..

include ../../../common/make.inc

CFLAGS ?= -W -Wall -g
CFLAGS+=-D_DEBUG
CFLAGS+=-DJSON_IS_AMALGAMATION
CFLAGS+=-DNO_DAP_CONFIG

OUT_FILE := xtpra-test
TARGET := $(OUT_DIR)/$(OUT_FILE)
TARGET_SRC := xtpra-test.cpp pca9554.cpp sysgpio.cpp

.PHONY: clean

DAP_SRC:=../../reader/Thread.cpp ../../reader/I2C.cpp \
	../../reader/Event.cpp ../../reader/WavFile.cpp

RGBTEST_SRC := ../rgbtest/Sound.cpp ../rgbtest/log.cpp

$(TARGET): $(TARGET_SRC) $(DAP_SRC) $(RGBTEST_SRC)
	$(CXX) $(CFLAGS) $(LDFLAGS) -I. -I../../dap-reader -lasound -o $@ $^

clean:
	rm -f $(TARGET)
