OUT_DIR:=../out
ARAGO:=../..

include ../../common/make.inc


CFLAGS ?= -W -Wall -g


SENSORTEST = $(OUT_DIR)/sensortest
LEDTEST    = $(OUT_DIR)/ledtest
REMOTE_EEPROM = $(OUT_DIR)/remote-eeprom

.PHONY: all sensortest ledtest rgbtest xtpra remote-eeprom clean


all: $(SENSORTEST) $(LEDTEST) $(REMOTE_EEPROM) rgbtest

sensortest: $(SENSORTEST)
$(SENSORTEST): sensortest.c i2c.c
	$(CXX) $(CFLAGS) $(LDFLAGS) -o $@ $^

ledtest: $(LEDTEST)
$(LEDTEST): ledtest.c i2c.c
	$(CXX) $(CFLAGS) $(LDFLAGS) -o $@ $^

remote-eeprom: $(REMOTE_EEPROM)
$(REMOTE_EEPROM): remote-eeprom.cpp
	$(CXX) $(CFLAGS) $(LDFLAGS) -o $@ $^

rgbtest xtpra:
	@ $(MAKE) -C $@


sound: alsa.c
	$(CXX) $(CFLAGS) $(LDFLAGS) -lasound -o $(OUT_DIR)/$@ $^


clean:
	rm -f $(SENSORTEST) $(LEDTEST) $(REMOTE_EEPROM)
	@ $(MAKE) -C rgbtest clean
	@ $(MAKE) -C xtpra clean
