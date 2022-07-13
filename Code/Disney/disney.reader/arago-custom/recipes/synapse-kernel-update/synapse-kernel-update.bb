DESCRIPTION = "Synapse kernel update package"
SECTION = "kernel"



PR = "r3"

SRC_URI = "\
"

RDEPENDS="kernel"
DEPENDS="virtual/kernel"

# Grab version info                                                                                         
python __anonymous() {
    import subprocess
    import os

    oebase = bb.data.getVar('OEBASE',d)
    oebase = bb.data.expand(oebase,d)

    machine = bb.data.getVar('MACHINE',d)
    machine = bb.data.expand(machine,d)

    # Change source dir and flashing args based on MACHINE type
    if machine == 'overo':
        base_dir = oebase + '/kernel'
        nflags = '-a -p'
        part = '/dev/mtd3'
    elif machine == 'xbr4':
        base_dir = oebase + '/xbr4/linux-am33x'
        nflags = '-p'
        part = '/dev/mtd6'
    else:
        raise Exception('Invalid MACHINE type')
    bb.data.setVar('S', base_dir, d)
    bb.data.setVar('NANDFLAGS', nflags, d)
    bb.data.setVar('PART', part, d)
    
    cwd = os.getcwd()
    os.chdir(base_dir)

    # Determine package version based on # of commits in kernel repo and the 
    # current truncated hash tag
    p = subprocess.Popen(["git", "rev-list", "--all", "--count"], stdout=subprocess.PIPE)
    version = p.communicate()[0].rstrip()
    if p.returncode != 0:
        raise Exception('Parse version script failed - ' + version)

    p = subprocess.Popen(["git", "rev-parse", "--short=8", "HEAD"], stdout=subprocess.PIPE)
    version += '-' + p.communicate()[0].rstrip()
    if p.returncode != 0:
        raise Exception('Parse version script failed')

    os.chdir(cwd)

    bb.data.setVar('PV', version, d)
}

# Stub
do_compile () {
}

do_install () {
    install -m 0644 arch/arm/boot/uImage ${D}/uImage
}

pkg_postinst_${PN} () {
    echo "Erasing flash on kernel partition"
    flash_eraseall ${PART}
    echo "Flashing kernel update - ${PV}"
    nandwrite ${NANDFLAGS} ${PART} /uImage
    echo "Kernel update completed"
}

PACKAGES= "${PN}"

FILES_${PN} = "/uImage \
"
