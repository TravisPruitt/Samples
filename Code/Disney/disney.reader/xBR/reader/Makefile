#
# Makefile
#
# Copyright (c) 2011-2012, synapse.com		
#

ARAGO:=../..
include version.inc
include ../../common/make.inc
include ../../common/version-string.inc

OBJ_DIR=o/
DEP_DIR=d/
OUT_DIR=../out/


CFLAGS:=-W -Wall
CFLAGS+=-pthread
CFLAGS+=-I.
CFLAGS+=-I../radio_driver
CFLAGS+=-D_DEBUG
CFLAGS+=-DJSON_IS_AMALGAMATION
CFLAGS+=-D__STDC_LIMIT_MACROS
CFLAGS+=-DFULL_VERSION="\"$(FULL_VERSION)\""

ifeq ($(firstword $(CXX)),arm-none-linux-gnueabi-g++)
	CFLAGS+=-DNATIVE
else
	CFLAGS+=-DPCLINUX
endif

LFLAGS:=-pthread
LFLAGS+=-ldl
LFLAGS+=-lcurl
LFLAGS+=-lresolv
LFLAGS+=$(LDFLAGS)

# Source files from which to create the application
SRCS = \
    grover.cpp \
    sysIface.cpp \
    config.cpp \
    ConfigFile.cpp \
    Radio.cpp \
    Radios.cpp \
    jsoncpp.cpp \
    log.cpp \
    EventBouncer.cpp \
    EventLogger.cpp \
    HelloThread.cpp \
    mongoose.c \
    webServ.cpp \
    pageEvent.cpp \
    pageApp.cpp \
    pageReader.cpp \
    HttpRequest.cpp \
    queryGetVar.cpp \
    ReaderInfo.cpp \
    Receiver.cpp \
    Transmitter.cpp \
    pageBands.cpp \
    PingEvent.cpp \
    ticks.cpp \
    Thread.cpp \
    ThreadEvent.cpp \
    Mutex.cpp \
    FileSystem.cpp \
    JsonParser.cpp \
    StringLib.cpp \
    UpgradeService.cpp \
    ihex.cpp \
    radioFlash.cpp \
    JsonEvent.cpp \
    StatusKeeper.cpp \
    xbrms.cpp \
    ns_parse.c


OBJS:=$(SRCS)
OBJS:=$(OBJS:%.cpp=$(OBJ_DIR)%.o)
OBJS:=$(OBJS:%.c=$(OBJ_DIR)%.o)

DEPS:=$(SRCS)
DEPS:=$(DEPS:%.cpp=$(DEP_DIR)%.d)
DEPS:=$(DEPS:%.c=$(DEP_DIR)%.d)


# Rules for building Gonzo (dap-reader)
$(OUT_DIR)grover: $(OBJS) Makefile
	@[ -d $(OUT_DIR) ] || mkdir -p $(OUT_DIR)
	$(CXX) $(OBJS) -o $@ $(LFLAGS) $(LIBS)

$(OBJ_DIR)%.o:%.cpp
	@[ -d $(OBJ_DIR) ] || mkdir -p $(OBJ_DIR)
	$(CXX) $(CFLAGS) -c $< -o $@

$(OBJ_DIR)%.o:%.c
	@[ -d $(OBJ_DIR) ] || mkdir -p $(OBJ_DIR)
	@[ -d $(OBJ_DIR)/rfid ] || mkdir -p $(OBJ_DIR)/rfid
	$(CXX) $(CFLAGS) -c $< -o $@

# Clean
clean:
	-@rm -rf $(OBJ_DIR)
	-@rm -rf $(DEP_DIR)
	-@rm $(OUT_DIR)grover

# Rules to make .i files that show where include files come from
%.i:%.cpp
	$(CXX) $(CFLAGS) -E -o $@ $<

%.i:%.c
	$(CXX) $(CFLAGS) -E -o $@ $<

# Rules to make dependency files
depends: $(DEPS)

$(DEP_DIR)%.d:%.cpp
	@[ -d $(DEP_DIR) ] || mkdir -p $(DEP_DIR)
	@[ -d $(DEP_DIR)/rfid ] || mkdir -p $(DEP_DIR)/rfid
	$(CXX) $(CFLAGS) -MM -MT $(OBJ_DIR)$*.o -MF $@ $<

$(DEP_DIR)%.d:%.c
	@[ -d $(DEP_DIR) ] || mkdir -p $(DEP_DIR)
	@[ -d $(DEP_DIR)/rfid ] || mkdir -p $(DEP_DIR)/rfid
	$(CXX) $(CFLAGS) -MM -MT $(OBJ_DIR)$*.o -MF $@ $<

show:
	echo $(DEPS)

# Include dependency files only for targets that need them
ifeq ($(filter clean depends, $(MAKECMDGOALS)),)
-include $(DEPS)
endif

