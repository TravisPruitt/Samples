DESCRIPTION = "TestServer"
SECTION = "network"
DEPENDS = "curl synapse-setinterfaces"

PR = "r5"
PV = "0.0.1"

SRC_URI = "\
	file://test-server \
	file://updateReaderSoftware \
"

inherit update-rc.d

INITSCRIPT_NAME = "test-server"
INITSCRIPT_PARAMS = "start 75 S . stop 31 0 6 ."

inherit synapse-version
SYNAPSE_APPLICATIONS = "test-server"

do_compile () {
	unset CFLAGS
	oe_runmake clean
	oe_runmake
}

do_install () {
	install -d ${D}${bindir}
	install -m 755 test-server ${D}/${bindir}/test-server
	install -m 755 ${WORKDIR}/updateReaderSoftware ${D}/${bindir}/updateReaderSoftware

	install -d ${D}${sysconfdir}/init.d
	install -m 0755 ${WORKDIR}/test-server ${D}${sysconfdir}/init.d
}

S = "${OEBASE}/xBR/test-server"
