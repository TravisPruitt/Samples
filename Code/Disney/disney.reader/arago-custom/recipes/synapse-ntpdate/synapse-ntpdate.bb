DESCRIPTION = "UDHCPC script for ntpdate if-up."
SECTION = "network"

PR = "r2"

SRC_URI = "\
	file://51ntpdate \
	file://51ntpdate-dhclient \
"

do_install () {
	install -d ${D}${sysconfdir}/udhcpc.d
	install -d ${D}${sysconfdir}/dhclient-exit-hooks.d
	install -m 0755 ${WORKDIR}/51ntpdate ${D}${sysconfdir}/udhcpc.d/51ntpdate
	install -m 0755 ${WORKDIR}/51ntpdate-dhclient ${D}${sysconfdir}/dhclient-exit-hooks.d/51ntpdate
}

