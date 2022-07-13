setenv load_mlo fatload mmc 0 ${loadaddr} MLO
setenv load_uboot fatload mmc 0 ${loadaddr} u-boot.bin
setenv load_kernel fatload mmc 0 ${loadaddr} uImage
setenv load_rootfs fatload mmc 0 ${loadaddr} rootfs.ubi
setenv erase_mlo nand erase 0 80000
setenv erase_uboot nand erase 80000 200000
setenv erase_kernel nand erase 280000 400000
setenv erase_rootfs nand erase 680000 1F980000

if mmc rescan 0; then
	nandecc hw;
	if run load_mlo; then run erase_mlo; nand write ${loadaddr} 0 ${filesize}; fi;
	nandecc sw;
	if run load_uboot; then run erase_uboot; nand write ${loadaddr} 80000 ${filesize}; fi;
	if run load_kernel; then run erase_kernel; nand write ${loadaddr} 280000 ${filesize}; fi;
	if run load_rootfs; then run erase_rootfs; nand write ${loadaddr} 680000 ${filesize}; fi;
	i2c mw 0x4a 0xee 0;
fi
