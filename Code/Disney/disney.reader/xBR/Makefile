#
# Makefile
#

OUT_DIR=./out
GROVER=out/grover
RADIO-CLI=out/radio-cli
SPI-TEST=out/spi-test


# Ensure output directory exists
$(shell mkdir -p $(OUT_DIR))

.PHONY: all
all: $(GROVER) $(RADIO-CLI) $(SPI-TEST)

.PHONY: help
help:
	@echo "Available targets"
	@echo "    all : (default)"
	@echo "    grover"
	@echo "    radio-cli"
	@echo "    spi-test"
	@echo "    clean"


.PHONY: grover
grover: $(GROVER)
$(GROVER):
	@ echo "---Reader---"
	@ $(MAKE) -C reader

.PHONY: radio-cli
radio-cli: $(RADIO-CLI)
$(RADIO-CLI):
	$(MAKE) -C cli-over-spi

.PHONY: spi-test
spi-test: $(SPI-TEST)
$(SPI-TEST):
	$(MAKE) -C spi-test

.PHONY: clean
clean:
	@echo "---clean---"
	make -C reader clean
	make -C cli-over-spi clean
	make -C spi-test clean
