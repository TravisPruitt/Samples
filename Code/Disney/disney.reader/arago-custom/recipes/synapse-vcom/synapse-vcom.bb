DESCRIPTION = "Lumidigm VCOM driver"
PV="1.2"
PR = "r1"
BRANCH = "master"
PR_append = "+gitr${SRCPV}"
SRCREV = "5b3eab2c253ef23199fa53ca5944b2edabcaa9da"

SRC_URI = "git://git@git.synapse.com/disney.dap-reader.git;protocol=ssh;branch=${BRANCH}"

inherit module

do_install_append() {
	install -d ${D}${sysconfdir}/udev/rules.d/
	install -m 644 45-vcom.rules ${D}${sysconfdir}/udev/rules.d/
}

S = "${OEBASE}/xFP/Lumidigm-driver"
