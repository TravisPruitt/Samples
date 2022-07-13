#
# Makefile
#

OUT_DIR=./out

# Ensure output directory exists
$(shell mkdir -p $(OUT_DIR))

.PHONY: all
all: dap-reader reader lib-VCOM sensortest ledtest rgbtest xbiotest rfidtest xtpra remote-eeprom

.PHONY: help
help:
	@echo "Available targets"
	@echo "    all : (default)"
	@echo "    dap-reader"
	@echo "    lib-VCOM : Lumidigm xbio VCOM library"
	@echo "    test-server : program for testing dap-reader"
	@echo "    sensortest"
	@echo "    rgbtest"
	@echo "    ledtest"
	@echo "    xbiotest"
	@echo "    clean"


.PHONY: dap-reader
dap-reader: lib-VCOM
	@echo "---DAP-Reader---"
	@cd dap-reader && make

.PHONY: reader
reader: lib-VCOM
	@echo "---Reader---"
	@cd reader && make

.PHONY: lib-VCOM
lib-VCOM:
	@echo "---Lumidigm---"
	@cd Lumidigm-lib && make
	@cd Lumidigm-lib2 && make

.PHONY: test-server
test-server:
	@echo "---test-server---"
	@cd test-server && make

.PHONY: rgbtest ledtest sensortest xtpra remote-eeprom
rgbtest ledtest sensortest xtpra remote-eeprom:
	@echo "---$@---"
	@$(MAKE) -C target-tools $@

.PHONY: xbiotest
xbiotest: lib-VCOM
	@echo "---xbiotest---"
	@cd xbiotest && make

.PHONY: rfidtest
rfidtest:
	@echo "---rfidtest---"
	@cd rfidtest && make

.PHONY: clean
clean:
	@echo "---clean---"
	@cd dap-reader && make clean
	@cd reader && make clean
	@cd Lumidigm-lib && make clean
	@cd Lumidigm-lib2 && make clean
	@cd test-server && make clean
	@cd target-tools && make clean
	@cd xbiotest && make clean
	@cd rfidtest && make clean
