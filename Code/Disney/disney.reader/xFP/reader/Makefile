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
CFLAGS+=-I../Lumidigm-lib2/API
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
    BiometricReaderDevice.cpp \
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
    JsonParser.cpp \
    LightEffects.cpp \
    log.cpp \
    Media.cpp \
    mongoose.c \
    Mutex.cpp \
    Sensors.cpp \
    SensorBus.cpp \
    SerialPortLinux.cpp \
    Sound.cpp \
    StatusKeeper.cpp \
    StringLib.cpp \
    TestThread.cpp \
    ticks.c \
    Thread.cpp \
    WavFile.cpp \
    WebServer.cpp \
    UpgradeService.cpp \
    GPIOTrigger.cpp \
    xbrms.cpp \
    ns_parse.c \
    rfid/RFIDReader.cpp \
    rfid/AES128Key.cpp \
    rfid/CRC16.cpp \
    rfid/CRC32.cpp \
    rfid/Desfire.cpp \
    rfid/DesKey.cpp \
    rfid/RFIDTag.cpp \
    rfid/SAM.cpp \
    rfid/SAMKey.cpp \
    rfid/SynapseReader.cpp \
    rfid/TDA8029.cpp \
    rfid/UltralightC.cpp \
    rgb/tlc59116.cpp \
    rgb/rgbled.cpp \
    rgb/rgbcluster.cpp \
    rgb/rgbcolor.cpp \
    rgb/ColorEffect.cpp \
    rgb/LedScript.cpp


LIBS = \
    ../lib/libVCOMEx2.lib

OBJS:=$(SRCS)
OBJS:=$(OBJS:%.cpp=$(OBJ_DIR)%.o)
OBJS:=$(OBJS:%.c=$(OBJ_DIR)%.o)

DEPS:=$(SRCS)
DEPS:=$(DEPS:%.cpp=$(DEP_DIR)%.d)
DEPS:=$(DEPS:%.c=$(DEP_DIR)%.d)


# Rules for building Gonzo (reader)
$(OUT_DIR)reader: $(OBJS) Makefile
	@[ -d $(OUT_DIR)$(*D) ] || mkdir -p $(OUT_DIR)$(*D)
	$(CXX) $(OBJS) -o $@ $(LFLAGS) $(LIBS)

$(OBJ_DIR)%.o:%.cpp
	@[ -d $(OBJ_DIR)$(*D) ] || mkdir -p $(OBJ_DIR)$(*D)
	$(CXX) $(CFLAGS) -c $< -o $@

$(OBJ_DIR)%.o:%.c
	@[ -d $(OBJ_DIR)$(*D) ] || mkdir -p $(OBJ_DIR)$(*D)
	$(CXX) $(CFLAGS) -c $< -o $@

# Clean
clean:
	-@rm -rf $(OBJ_DIR)
	-@rm -rf $(DEP_DIR)
	-@rm $(OUT_DIR)reader

# Rules to make .i files that show where include files come from
%.i:%.cpp
	$(CXX) $(CFLAGS) -E -o $@ $<

%.i:%.c
	$(CXX) $(CFLAGS) -E -o $@ $<

# Rules to make dependency files
depends: $(DEPS)

$(DEP_DIR)%.d:%.cpp
	@[ -d $(DEP_DIR)$(*D) ] || mkdir -p $(DEP_DIR)$(*D)
	$(CXX) $(CFLAGS) -MM -MT $(OBJ_DIR)$*.o -MF $@ $<

$(DEP_DIR)%.d:%.c
	@[ -d $(DEP_DIR)$(*D) ] || mkdir -p $(DEP_DIR)$(*D)
	$(CXX) $(CFLAGS) -MM -MT $(OBJ_DIR)$*.o -MF $@ $<

show:
	echo $(DEPS)

# Include dependency files only for targets that need them
ifeq ($(filter clean depends, $(MAKECMDGOALS)),)
-include $(DEPS)
endif

