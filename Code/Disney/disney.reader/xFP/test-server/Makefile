#
# Makefile for DAP reader
# Greg Strange
# 2011 July 20
#

CXX?=$(CROSS_COMPILE)g++
#CC?=$(CROSS_COMPILE)gcc
OBJ_DIR=o/
DEP_DIR=d/
OUT_DIR=../out/

CFLAGS:=-W -Wall
CFLAGS+=-pthread
CFLAGS+=-I.
CFLAGS+=-I../Lumidigm/API
CFLAGS+=-D_DEBUG
CFLAGS+=-DJSON_IS_AMALGAMATION
CFLAGS+=-D__STDC_LIMIT_MACROS

LFLAGS:=-pthread
LFLAGS+=-ldl
LFLAGS+=-lcurl


SRCS = \
	main.cpp \
    Console.cpp \
    HttpRequest.cpp \
    jsoncpp.cpp \
    log.cpp \
    mongoose.c \
    ticks.cpp \
    WebServer.cpp

OBJS:=$(SRCS)
OBJS:=$(OBJS:%.cpp=$(OBJ_DIR)%.o)
OBJS:=$(OBJS:%.c=$(OBJ_DIR)%.o)

DEPS:=$(SRCS)
DEPS:=$(DEPS:%.cpp=$(DEP_DIR)%.d)
DEPS:=$(DEPS:%.c=$(DEP_DIR)%.d)


# Rules to build app
$(OUT_DIR)test-server: $(OBJS) Makefile
	@[ -d $(OUT_DIR) ] || mkdir -p $(OUT_DIR)
	$(CXX) $(OBJS) -o $@ $(LFLAGS)

$(OBJ_DIR)%.o:%.cpp
	@[ -d $(OBJ_DIR) ] || mkdir -p $(OBJ_DIR)
	$(CXX) $(CFLAGS) -c $< -o $@

$(OBJ_DIR)%.o:%.c
	@[ -d $(OBJ_DIR) ] || mkdir -p $(OBJ_DIR)
	$(CXX) $(CFLAGS) -c $< -o $@

#Clean
.PHONY: clean
clean:
	-@rm $(OBJ_DIR)*
	-@rmdir $(OBJ_DIR)
	-@rm $(DEP_DIR)*
	-@rmdir $(DEP_DIR)
	-@rm $(OUT_DIR)test-server

# Rules to make .i files that show where include files come from
%.i:%.cpp
	$(CXX) $(CFLAGS) -E -o $@ $<

%.i:%.c
	$(CXX) $(CFLAGS) -E -o $@ $<

# Rules to make dependency files
.PHONY: depends
depends: $(DEPS)

$(DEP_DIR)%.d:%.cpp
	@[ -d $(DEP_DIR) ] || mkdir -p $(DEP_DIR)
	$(CXX) $(CFLAGS) -MM -MT $(OBJ_DIR)$*.o -MF $@ $<

$(DEP_DIR)%.d:%.c
	@[ -d $(DEP_DIR) ] || mkdir -p $(DEP_DIR)
	$(CXX) $(CFLAGS) -MM -MT $(OBJ_DIR)$*.o -MF $@ $<


# Include dependency files only for targets that need them
ifeq ($(filter clean depends, $(MAKECMDGOALS)),)
-include $(DEPS)
endif
