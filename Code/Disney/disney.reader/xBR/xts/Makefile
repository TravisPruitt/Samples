#
# Makefile
#	xBR project
#		test-server		- A PC application used to test communication with a reader. 
#
#	copyright Synapse Product Development	info@synapse.com
# 	2011 - 2012		
#

TARGET=friendlier

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
LFLAGS+=$(LDFLAGS)

# Source files from which to create the application
SRCS = \
test-server.cpp \
config.cpp \
Console.cpp \
common.cpp \
jsoncpp.cpp \
logging.cpp \
sendHttp.cpp \
mongoose.c \
Mutex.cpp \
webServ.cpp \
pages.cpp \
pageStream.cpp \
HttpRequest.cpp \
queryGetVar.cpp \
command.cpp \


OBJS:=$(SRCS)
OBJS:=$(OBJS:%.cpp=$(OBJ_DIR)%.o)
OBJS:=$(OBJS:%.c=$(OBJ_DIR)%.o)

DEPS:=$(SRCS)
DEPS:=$(DEPS:%.cpp=$(DEP_DIR)%.d)
DEPS:=$(DEPS:%.c=$(DEP_DIR)%.d)


# Rules for building Gonzo (dap-reader)
$(OUT_DIR)xts: $(OBJS) Makefile
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
	-@rm $(OUT_DIR)xts

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

