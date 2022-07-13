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
LFLAGS+=$(LDFLAGS)



SRCS = \
    AES128Key.cpp \
    CRC16.cpp \
    CRC32.cpp \
    Desfire.cpp \
    DesKey.cpp \
    jsoncpp.cpp \
    JsonParser.cpp \
    log.cpp \
    main.cpp \
    RFIDTag.cpp \
    SAM.cpp \
    SAMKey.cpp \
    SerialPortLinux.cpp \
    StringLib.cpp \
    SynapseReader.cpp \
    TDA8029.cpp \
    ticks.cpp \
    UltralightC.cpp 

LIBS = 

OBJS:=$(SRCS)
OBJS:=$(OBJS:%.cpp=$(OBJ_DIR)%.o)
OBJS:=$(OBJS:%.c=$(OBJ_DIR)%.o)

DEPS:=$(SRCS)
DEPS:=$(DEPS:%.cpp=$(DEP_DIR)%.d)
DEPS:=$(DEPS:%.c=$(DEP_DIR)%.d)


# Rules for building Gonzo (reader)
$(OUT_DIR)rfidtest: $(OBJS) Makefile
	@[ -d $(OUT_DIR) ] || mkdir -p $(OUT_DIR)
	$(CXX) $(OBJS) -o $@ $(LFLAGS) $(LIBS)

$(OBJ_DIR)%.o:%.cpp
	@[ -d $(OBJ_DIR) ] || mkdir -p $(OBJ_DIR)
	$(CXX) $(CFLAGS) -c $< -o $@

$(OBJ_DIR)%.o:%.c
	@[ -d $(OBJ_DIR) ] || mkdir -p $(OBJ_DIR)
	@[ -d $(OBJ_DIR)/rfidtest ] || mkdir -p $(OBJ_DIR)/rfidtest
	$(CXX) $(CFLAGS) -c $< -o $@

# Clean
clean:
	-@rm -rf $(OBJ_DIR)
	-@rm -rf $(DEP_DIR)
	-@rm $(OUT_DIR)rfidtest

# Rules to make .i files that show where include files come from
%.i:%.cpp
	$(CXX) $(CFLAGS) -E -o $@ $<

%.i:%.c
	$(CXX) $(CFLAGS) -E -o $@ $<

# Rules to make dependency files
depends: $(DEPS)

$(DEP_DIR)%.d:%.cpp
	@[ -d $(DEP_DIR) ] || mkdir -p $(DEP_DIR)
	$(CXX) $(CFLAGS) -MM -MT $(OBJ_DIR)$*.o -MF $@ $<

$(DEP_DIR)%.d:%.c
	@[ -d $(DEP_DIR) ] || mkdir -p $(DEP_DIR)
	$(CXX) $(CFLAGS) -MM -MT $(OBJ_DIR)$*.o -MF $@ $<

show:
	echo $(DEPS)

# Include dependency files only for targets that need them
ifeq ($(filter clean depends, $(MAKECMDGOALS)),)
-include $(DEPS)
endif

