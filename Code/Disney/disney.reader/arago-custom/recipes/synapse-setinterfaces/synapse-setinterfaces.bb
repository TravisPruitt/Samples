DESCRIPTION = "override /etc/network/interfaces file during init"
SECTION = "network"

PR = "r2"

SRC_URI = "\
	file://setinterfaces \
"

inherit update-rc.d

INITSCRIPT_NAME = "setinterfaces"
INITSCRIPT_PARAMS = "start 30 S . stop 31 0 6 ."


do_install () {
	install -d ${D}${sysconfdir}/init.d
	install -m 0755 ${WORKDIR}/setinterfaces ${D}${sysconfdir}/init.d
}

