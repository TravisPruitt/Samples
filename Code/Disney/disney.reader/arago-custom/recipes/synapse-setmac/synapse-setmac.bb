DESCRIPTION = "Mac setter, set mac from omap3 DIEID or to EEPROM"
SECTION = "network"

PR = "r3"

SRC_URI = "\
	file://setmac \
	file://setmac-eeprom \
"

inherit update-rc.d

INITSCRIPT_NAME = "setmac"
INITSCRIPT_PARAMS = "start 30 S . stop 31 0 6 ."


do_install () {
	install -d ${D}${sysconfdir}/init.d
	install -m 0755 ${WORKDIR}/setmac ${D}${sysconfdir}/init.d
	install -d ${D}${bindir}
	install -m 0755 ${WORKDIR}/setmac-eeprom ${D}${bindir}
}
