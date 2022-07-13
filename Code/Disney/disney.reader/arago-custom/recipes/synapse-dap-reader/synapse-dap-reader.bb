DESCRIPTION = "Dap reader"
SECTION = "network"

require deps.inc

DEPENDS = "alsa-utils curl"
RDEPENDS_${PN} = "${DEPS}"

PR = "r15"

SRC_URI = "\
	file://dap-reader \
"

inherit update-rc.d
INITSCRIPT_NAME = "dap-reader"
INITSCRIPT_PARAMS = "start 75 5 . stop 31 0 6 ."

# Grab version info
python __anonymous() {
    import subprocess

    base_dir = bb.data.getVar('OEBASE',d,'')
    vscript = base_dir + "/tools/get_version.sh"

    p = subprocess.Popen(["bash", vscript, "dap-reader"], stdout=subprocess.PIPE)
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
	install -m 755 out/dap-reader ${D}/mayhem/dap-reader
	install -m 755 out/ledtest ${D}/mayhem/ledtest
	install -m 755 out/sensortest ${D}/mayhem/sensortest
	install -m 755 out/xbiotest ${D}/mayhem/xbiotest
	cp -r dap-reader/target-files/* ${D}/mayhem
	cp -r target-scripts/* ${D}/mayhem
	install -d ${D}/home/root
	cp dap-reader/target-files/root_profile ${D}/home/root/.profile

	install -d ${D}${sysconfdir}/init.d
	install -m 0755 ${WORKDIR}/dap-reader ${D}${sysconfdir}/init.d
}

PACKAGES= "${PN}-dbg ${PN}"

FILES_${PN}-dbg += "/mayhem/.debug"

FILES_${PN} = "/mayhem/* \
               /home/root/.profile \ 
               ${sysconfdir}/init.d/dap-reader \
               "

S = "${OEBASE}/xFP" 
