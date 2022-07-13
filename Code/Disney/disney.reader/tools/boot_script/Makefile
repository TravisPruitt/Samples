OEBASE ?= ../..

PATH := $(OEBASE)/arago-tmp/sysroots/x86_64-linux/usr/bin:$(PATH)
PATH := $(OEBASE)/arago-tmp/sysroots/i686-linux/usr/bin:$(PATH)

boot.scr: flash_all.cmd Makefile
	mkimage -A arm -O linux -T script -C none -a 0 -e 0 -n "Flash NAND" -d $< $@
