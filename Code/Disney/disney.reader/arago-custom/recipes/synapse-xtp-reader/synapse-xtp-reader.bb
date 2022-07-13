DESCRIPTION = "xTP reader"
SECTION = "network"

require deps.inc

DEPENDS = "alsa-utils curl"
RDEPENDS_${PN} = "${DEPS}"

PR = "r1"

SRC_URI = "\
	file://reader \
"

inherit update-rc.d
INITSCRIPT_NAME = "reader"
INITSCRIPT_PARAMS = "start 75 5 . stop 31 0 6 ."

# Grab version info
python __anonymous() {
    import subprocess

    base_dir = bb.data.getVar('OEBASE',d,'')
    vscript = base_dir + "/tools/get_version.sh"

    p = subprocess.Popen(["bash", vscript, "xtp-reader"], stdout=subprocess.PIPE)
    version = p.communicate()[0].rstrip()
    if p.returncode != 0:
        raise Exception('Parse version script failed - ' + version)

    bb.data.setVar('PV', version, d)
}

do_compile () {
	unset CFLAGS
	oe_runmake clean
	oe_runmake
}

do_install () {
	install -d ${D}/mayhem
	install -m 755 out/reader ${D}/mayhem/reader
	install -m 755 out/sensortest ${D}/mayhem/sensortest
	install -m 755 out/xbiotest ${D}/mayhem/xbiotest
    install -m 755 out/rgbtest ${D}/mayhem/rgbtest
    install -m 755 out/rfidtest ${D}/mayhem/rfidtest
    install -m 755 out/xtpra-test ${D}/mayhem/xtpra-test
    install -m 755 out/remote-eeprom ${D}/mayhem/remote-eeprom
	cp -r reader/target-files/* ${D}/mayhem
	cp -r reader/target-scripts/* ${D}/mayhem
	install -d ${D}/home/root
	cp reader/target-files/root_profile ${D}/home/root/.profile

	install -d ${D}${sysconfdir}/init.d
	install -m 0755 ${WORKDIR}/reader ${D}${sysconfdir}/init.d
}

PACKAGES= "${PN}-dbg ${PN}"

FILES_${PN}-dbg += "/mayhem/.debug"

FILES_${PN} = "/mayhem/* \
               /home/root/.profile \ 
               ${sysconfdir}/init.d/reader \
               "

S = "${OEBASE}/xFP"
