DESCRIPTION = "Task for development tools"
LICENSE = "MIT"
PR = "r3"

inherit task

MAYHEM_DEV = "\
    curl-dev \
    gcc \
    gcc-symlinks \
    g++-symlinks \
    cpp \
    binutils \
    gdbserver \
    gdb \
    linux-libc-headers-dev \
    "

# cppstub is needed to install libstdc++ in the image
MAYHEM_DEVELOPMENT = "\
    ${MAYHEM_DEV} \
    "

RDEPENDS_${PN} = "\
    ${MAYHEM_DEVELOPMENT} \
    "
