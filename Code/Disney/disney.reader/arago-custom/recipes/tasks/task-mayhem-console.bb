DESCRIPTION = "Extended task to get more basic and console apps"
LICENSE = "MIT"
PR = "r11"

inherit task

MAYHEM_NCURSES = "\
    ncurses \
    ncurses-terminfo \
    ncurses-tools \
    "

MAYHEM_FSTOOLS = "\
    e2fsprogs \
    e2fsprogs-e2fsck \
    e2fsprogs-mke2fs \
    dosfstools \
    util-linux-ng-fdisk \
    "

MAYHEM_UTILS = "\
    usbutils \
    i2c-tools \
    iproute2 \
    iperf \
    ethtool \
    nano \
    "

MAYHEM_DVSDK_PREREQ = "\
    zlib \
    libpng12 \
    cppstub \
    "

MAYHEM_SSH = "\
    openssh-ssh \
    openssh-sshd \
    openssh-scp \
    openssh-keygen \
    openssh-sftp \
    openssh-sftp-server \
    synapse-sshkey \
    "

# cppstub is needed to install libstdc++ in the image
MAYHEM_CONSOLE = "\
    ${MAYHEM_NCURSES} \
    ${MAYHEM_FSTOOLS} \
    ${MAYHEM_UTILS} \
    ${MAYHEM_DVSDK_PREREQ} \
    ${MAYHEM_SSH} \
    "

RDEPENDS_${PN} = "\
    ${MAYHEM_CONSOLE} \
    "

RRECOMMENDS_${PN} = "\
    kernel-modules \
    "
