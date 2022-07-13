DESCRIPTION = "Grover"
SECTION = "network"

require deps.inc

DEPENDS = "curl ncurses"
RDEPENDS_${PN} = "${DEPS}"
RREPLACES = "synapse-radio-cli synapse-spi-test"

PR = "r11"

SRC_URI = "\
    file://grover \
    file://monitor-grover \
    file://root_profile \
"

inherit update-rc.d
INITSCRIPT_NAME = "grover"
INITSCRIPT_PARAMS = "start 75 5 . stop 31 0 6 ."

# Grab version info
python __anonymous() {
    import subprocess

    base_dir = bb.data.getVar('OEBASE',d,'')
    vscript = base_dir + "/tools/get_version.sh"
    p = subprocess.Popen(["bash", vscript, "grover"], stdout=subprocess.PIPE)
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
    # install application
    install -d ${D}/mayhem
    install -m 755 out/grover ${D}/mayhem/grover

    install -d ${D}/mayhem/www

    # install monitor script
    install -m 755 ${FILE_DIRNAME}/files/monitor-grover ${D}/mayhem/monitor-grover

    # install init script
    install -d ${D}${sysconfdir}/init.d
    install -m 0755 ${FILE_DIRNAME}/files/grover ${D}${sysconfdir}/init.d/grover

    # install root .profile
    install -d ${D}/home/root
    cp ${FILE_DIRNAME}/files/root_profile ${D}/home/root/.profile

    # install eth1-ctrl script
    install -m 0755 ${FILE_DIRNAME}/files/eth1-ctrl ${D}/mayhem/eth1-ctrl

    # install misc files
    cp -r target-files/* ${D}/mayhem

    # install tools
    install -m 755 out/radio-cli ${D}/mayhem/radio-cli
    install -m 755 out/spi-test ${D}/mayhem/spi-test
    install -m 755 out/mfg-spi-test ${D}/mayhem/mfg-spi-test
}

PACKAGES= "${PN}-dbg ${PN}"

FILES_${PN}-dbg += "/mayhem/.debug"

FILES_${PN} = "/mayhem/* \
               /home/root/.profile \
               ${sysconfdir}/init.d/grover \
               "

S = "${OEBASE}/xBR"
