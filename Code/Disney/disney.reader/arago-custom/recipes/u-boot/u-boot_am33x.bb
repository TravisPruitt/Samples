require u-boot.inc
PR = "r5"

S = "${OEBASE}/xbr4/u-boot-am33x"

COMPATIBLE_MACHINE = "xbr4"

UBOOT_BINARY = "u-boot.img"
UBOOT_IMAGE = "u-boot-${MACHINE}-${PV}-${PR}.img"
UBOOT_SYMLINK = "u-boot-${MACHINE}.img"
