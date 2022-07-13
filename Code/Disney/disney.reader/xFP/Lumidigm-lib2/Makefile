#
# Makefile for Lumidigm library
# August, 2012
#


ARAGO=../..
include ../../common/make.inc


SRC_DIR=src/
OBJ_DIR=o/
OUT_DIR=../lib

TARG=$(OUT_DIR)/libVCOMEx2.lib

CFLAGS:=-W -Wall -g -fPIC -Wno-unused-parameter
CFLAGS+=-Iinclude
CFLAGS+=-IAPI
#CFLAGS+=-D_DEBUG
CFLAGS+=-D_GCC=1
CFLAGS+=-D __SUPPORT_WIN32_USB__
CFLAGS+=-D _VCOM_EXAMPLE


SRCS = \
    VCOMCore.cpp \
    VCOMDiag.cpp \
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


OBJS:=$(SRCS)
OBJS:=$(OBJS:%.cpp=$(OBJ_DIR)%.o)
SRCS:=$(SRCS:%=$(SRC_DIR)%)

$(TARG): $(OBJS)
	@ mkdir -p $(OUT_DIR)
	$(AR) -cvq $@ $(OBJS)

.PHONY: clean
clean:
	- rm -fr ./o
	- rm $(TARG)

$(OBJ_DIR)%.o:$(SRC_DIR)%.cpp
	@ mkdir -p $(OBJ_DIR)
	$(CXX) $(CFLAGS) -c $< -o $@

%.i:%.cpp
	$(CXX) $(CFLAGS) -E -o $@ $<
