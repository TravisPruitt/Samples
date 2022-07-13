#
# Makefile for DAP reader
# Greg Strange
# 2011 July 20
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
CFLAGS+=-I./rfid
CFLAGS+=-I../Lumidigm-lib/API
CFLAGS+=-D_DEBUG
CFLAGS+=-DJSON_IS_AMALGAMATION
CFLAGS+=-D__STDC_LIMIT_MACROS
CFLAGS+=-DFULL_VERSION="\"$(FULL_VERSION)\""

# Let mongoose know where openssl libs are located
CFLAGS+=-DSSL_LIB=\"libssl.so.1.0.0\"
CFLAGS+=-DCRYPTO_LIB=\"libcrypto.so.1.0.0\"

LFLAGS:=-pthread
LFLAGS+=-ldl
LFLAGS+=-lcurl
LFLAGS+=-lasound
LFLAGS+=-lresolv
LFLAGS+=$(LDFLAGS)



SRCS = \
	main.cpp \
    base64.cpp \
    BiometricReader.cpp \
	ConfigFile.cpp \
    DapConfig.cpp \
    DapReader.cpp \
    Event.cpp \
    EventLogger.cpp \
	FileSystem.cpp \
    HelloThread.cpp \
    HttpRequest.cpp \
    I2C.cpp \
    jsoncpp.cpp \
    jsonDebugWriter.cpp \
    LightEffects.cpp \
    log.cpp \
    mongoose.c \
    Mutex.cpp \
    PWM.cpp \
    Sensors.cpp \
    SensorBus.cpp \
    SerialPortLinux.cpp \
    Sound.cpp \
    StatusKeeper.cpp \
    TestThread.cpp \
    ticks.c \
    Thread.cpp \
    WavFile.cpp \
    WebServer.cpp \
    UpgradeService.cpp \
    xbrms.cpp \
    ns_parse.c \
    rfid/CRC16.cpp \
    rfid/Desfire.cpp \
    rfid/FeigReader.cpp \
    rfid/RFIDReader.cpp \
    rfid/RFIDTag.cpp \
    rfid/SAM.cpp \
    rfid/SAMKey.cpp \
    rfid/T1Frame.cpp \
    rfid/UltralightC.cpp


LIBS = \
    ../lib/libVCOMEx.lib

OBJS:=$(SRCS)
OBJS:=$(OBJS:%.cpp=$(OBJ_DIR)%.o)
OBJS:=$(OBJS:%.c=$(OBJ_DIR)%.o)

DEPS:=$(SRCS)
DEPS:=$(DEPS:%.cpp=$(DEP_DIR)%.d)
DEPS:=$(DEPS:%.c=$(DEP_DIR)%.d)


# Rules for building Gonzo (dap-reader)
$(OUT_DIR)dap-reader: $(OBJS) Makefile
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
	-@rm $(OUT_DIR)dap-reader

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

