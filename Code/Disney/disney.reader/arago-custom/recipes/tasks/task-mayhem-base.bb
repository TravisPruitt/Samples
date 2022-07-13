DESCRIPTION = "Basic task to get a device booting"
LICENSE = "MIT"
PR = "r6"

inherit task

# those ones can be set in machine config to supply packages needed to get machine booting
MACHINE_ESSENTIAL_EXTRA_RDEPENDS ?= ""
MACHINE_ESSENTIAL_EXTRA_RRECOMMENDS ?= ""

MAYHEM_BASE = "\
    mtd-utils \
    curl \
    arago-feed-configs \
    devmem2 \
    ethtool \
    tcpdump \
    synapse-setmac \
    iputils-arping \
    ifplugd \
    synapse-ip-fallback \
    synapse-arping \
    "

# minimal set of packages - needed to boot
RDEPENDS_${PN} = "\
    base-files \
    base-passwd \
    busybox \
    initscripts \
    modutils-initscripts \
    netbase \
    update-alternatives \
    module-init-tools \
    ${MAYHEM_BASE} \
    ${MACHINE_ESSENTIAL_EXTRA_RDEPENDS} \
    "

RRECOMMENDS_${PN} = "\
    ${MACHINE_ESSENTIAL_EXTRA_RRECOMMENDS} \
    "
