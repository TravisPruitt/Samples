DESCRIPTION = "if-up.d script for sending arping"
SECTION = "network"

PR = "r1"

SRC_URI = "\
	file://arping \
"

do_install () {
	install -d ${D}${sysconfdir}/network/if-up.d
	install -m 0755 ${WORKDIR}/arping ${D}${sysconfdir}/network/if-up.d/arping
}
