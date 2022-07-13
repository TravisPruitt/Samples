DESCRIPTION = "Grover radio driver"
PR = "r2"
BRANCH = "master"

inherit module

# Grab version info
python __anonymous() {
    import subprocess

    base_dir = bb.data.getVar('OEBASE',d,'')
    vscript = base_dir + "/tools/get_version.sh"
    p = subprocess.Popen(["bash", vscript, "radio-driver"], stdout=subprocess.PIPE)
    version = p.communicate()[0].rstrip()
    if p.returncode != 0:
        raise Exception('Parse version script failed - ' + version)

    bb.data.setVar('PV', version, d)
}

do_compile_append() {
	oe_runmake radiotool radioflasher
}

do_install_append() {
        install -d ${D}${bindir}
	install -m 755 radiotool ${D}/${bindir}/radiotool
	install -m 755 radioflasher ${D}/${bindir}/radioflasher
# Install udev script for new multi-instance driver
	install -d ${D}${sysconfdir}/udev/rules.d
	install -m 544 grover.rules ${D}${sysconfdir}/udev/rules.d/grover.rules
}

FILES_${PN} += "${bindir}/radiotool ${bindir}/radioflasher ${sysconfdir}/udev/rules.d/grover.rules"

S = "${OEBASE}/xBR/radio_driver"
