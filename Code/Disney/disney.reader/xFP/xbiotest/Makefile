#
# Makefile for DAP reader
# Greg Strange
# 2011 July 20
#


ARAGO:=../..
include ../../common/make.inc


OBJ_DIR=o/
DEP_DIR=d/
OUT_DIR=../out/


CFLAGS:=-W -Wall
CFLAGS+=-pthread
CFLAGS+=-I.
CFLAGS+=-I../Lumidigm-lib/API
CFLAGS+=-I../Lumidigm-lib/include
CFLAGS+=-fPIC
CFLAGS+=-D_DEBUG
CFLAGS+=-D__STDC_LIMIT_MACROS
CFLAGS+=-D_GCC=1
CFLAGS+=-D__SUPPORT_WIN32_USB__
CFLAGS+=-D_VCOM_EXAMPLE

LFLAGS:=-pthread
LFLAGS+=-ldl
LFLAGS+=$(LDFLAGS)


SRCS = \
	VCOMDisneyApp.cpp \

LIBS = \
    ../lib/libVCOMEx.lib

OBJS:=$(SRCS)
OBJS:=$(OBJS:%.cpp=$(OBJ_DIR)%.o)
OBJS:=$(OBJS:%.c=$(OBJ_DIR)%.o)

DEPS:=$(SRCS)
DEPS:=$(DEPS:%.cpp=$(DEP_DIR)%.d)
DEPS:=$(DEPS:%.c=$(DEP_DIR)%.d)


# Rules for building Gonzo (dap-reader)
$(OUT_DIR)xbiotest: $(OBJS) Makefile
	@[ -d $(OUT_DIR) ] || mkdir -p $(OUT_DIR)
	$(CXX) $(OBJS) -o $@ $(LFLAGS) ../lib/libVCOMEx.lib

$(OBJ_DIR)%.o:%.cpp
	@[ -d $(OBJ_DIR) ] || mkdir -p $(OBJ_DIR)
	$(CXX) $(CFLAGS) -c $< -o $@

$(OBJ_DIR)%.o:%.c
	@[ -d $(OBJ_DIR) ] || mkdir -p $(OBJ_DIR)
	$(CXX) $(CFLAGS) -c $< -o $@

# Clean
clean:
	-@rm $(OBJ_DIR)/*
	-@rmdir $(OBJ_DIR)
	-@rm $(DEP_DIR)/*
	-@rmdir $(DEP_DIR)
	-@rm $(OUT_DIR)xbiotest

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

