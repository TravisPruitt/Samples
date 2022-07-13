DESCRIPTION = "Auto-generate self-signed SSL certificate"
SECTION = "base"

PR = "r1"
PV = "1.0.0"

SRC_URI = "\
	file://server.pem \
"

do_install () {
	install -d ${D}${libdir}/ssl
	install -m 0755 ${WORKDIR}/server.pem ${D}${libdir}/ssl/server.pem
}

FILES_${PN} = "${libdir}/ssl/server.pem"

S = "${WORKDIR}"
