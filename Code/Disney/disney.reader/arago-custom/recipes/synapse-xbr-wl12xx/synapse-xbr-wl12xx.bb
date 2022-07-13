DESCRIPTION = "Grover"
SECTION = "network"

DEPENDS = "ti-wifi-utils"

PR = "r2"

SRC_URI = "\
    file://wl127x-fw-3.bin \
    file://wl127x-fw-plt-3.bin \
    file://TIInit_7.6.15.bts \
    file://grover-wifi \
    file://grover.ini \
"

inherit update-rc.d
INITSCRIPT_NAME = "grover-wifi"
INITSCRIPT_PARAMS = "start 19 5 . stop 31 0 6 ."

do_install () {
    # install firmware
    install -d ${D}/lib/firmware/ti-connectivity
    install -m 755 ${WORKDIR}/wl127x-fw-3.bin ${D}/lib/firmware/ti-connectivity
    install -m 755 ${WORKDIR}/wl127x-fw-plt-3.bin ${D}/lib/firmware/ti-connectivity
    install -m 755 ${WORKDIR}/TIInit_7.6.15.bts ${D}/lib/firmware

    # install init script
    install -d ${D}${sysconfdir}/init.d
    install -m 0755 ${WORKDIR}/grover-wifi ${D}${sysconfdir}/init.d/grover-wifi

    # install wifi cal script
    install -d ${D}${datadir}/grover-wifi
    install -m 644 ${WORKDIR}/grover.ini ${D}${datadir}/grover-wifi/grover.ini
}

PACKAGES= "${PN}"

FILES_${PN} = "${sysconfdir}/init.d/grover-wifi \
		/lib/firmware/ti-connectivity/* \
		/lib/firmware/TIInit_7.6.15.bts \
        ${datadir}/grover-wifi/grover.ini"

S = "${OEBASE}/"
