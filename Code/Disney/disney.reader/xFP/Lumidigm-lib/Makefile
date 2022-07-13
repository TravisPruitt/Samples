#
# Makefile for Lumidigm library
# August 2, 2011
# Greg Strange
#


ARAGO=../..
include ../../common/make.inc


SRC_DIR=src/
OBJ_DIR=o/
OUT_DIR=../lib/

CFLAGS:=-W -Wall -g -fPIC -Wno-unused-parameter
#CFLAGS+=-pthread
CFLAGS+=-Iinclude
CFLAGS+=-IAPI
#CFLAGS+=-D_DEBUG
CFLAGS+=-D_GCC=1
CFLAGS+=-D __SUPPORT_WIN32_USB__
CFLAGS+=-D _VCOM_EXAMPLE


#LFLAGS:=-pthread
#LFLAGS+=-ldl


SRCS = \
	VCOMCore.cpp \
    TransportRS232.cpp \
    TransportRS232_NX.cpp \
    ICmd.cpp \
    TransportUSB.cpp \
    gccWindows.cpp \
    STranRS232.cpp \
    SerialComLinux.cpp \
    XModem.cpp \
    V100Cmd.cpp \
    V100DYCmd.cpp \
    V100IDCmd.cpp \
    V100CommandHandler.cpp \

#    MemoryManager.cpp \


OBJS:=$(SRCS)
OBJS:=$(OBJS:%.cpp=$(OBJ_DIR)%.o)
SRCS:=$(SRCS:%=$(SRC_DIR)%)

$(OUT_DIR)libVCOMEx.lib: $(OBJS)
	@[ -d $(OUT_DIR) ] || mkdir -p $(OUT_DIR)
	$(AR) -cvq $@ $(OBJS)
#	$(CXX) -shared -g -fPIC $(OBJS) -o $@

clean:
	-@rm o/*
	-@rmdir o
	-@rm $(OUT_DIR)libVCOMEx.lib

$(OBJ_DIR)%.o:$(SRC_DIR)%.cpp
	@[ -d $(OBJ_DIR) ] || mkdir -p $(OBJ_DIR)
	$(CXX) $(CFLAGS) -c $< -o $@

%.i:%.cpp
	$(CXX) $(CFLAGS) -E -o $@ $<

