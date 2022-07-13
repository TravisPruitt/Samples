DESCRIPTION = "if-up.d script for setting a fallback up"
SECTION = "network"

PR = "r1"

SRC_URI = "\
	file://fallback \
"

do_install () {
	install -d ${D}${sysconfdir}/network/if-up.d
	install -m 0755 ${WORKDIR}/fallback ${D}${sysconfdir}/network/if-up.d/fallback
}
