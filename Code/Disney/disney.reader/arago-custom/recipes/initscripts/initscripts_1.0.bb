DESCRIPTION = "SysV init scripts"
SECTION = "base"
PRIORITY = "required"
DEPENDS = "makedevs"
RDEPENDS_${PN} = "makedevs"
LICENSE = "GPL"
PR = "r128"

SRC_URI = "file://functions \
           file://halt \
           file://ramdisk \
           file://umountfs \
           file://devices \
           file://devpts.sh \
           file://devpts \
           file://hostname.sh \
           file://mountall.sh \
           file://banner \
           file://finish.sh \
           file://bootmisc.sh \
           file://mountnfs.sh \
           file://reboot \
           file://checkfs.sh \
           file://single \
           file://sendsigs \
           file://urandom \
           file://rmnologin \
           file://checkroot \
           file://umountnfs.sh \
           file://sysfs.sh \
           file://device_table.txt \
           file://populate-volatile.sh \
           file://volatiles \
           file://save-rtc.sh \
           file://save-rtc-uclibc.sh \
           file://rootfs-ro \
           file://rootfs-rw \
           file://pre-configure \
           file://post-configure"

SRC_URI_append_arm = " file://alignment.sh"

CONFFILES_${PN} += "${sysconfdir}/device_table"

KERNEL_VERSION = ""

do_install () {
#
# Create directories and install device independent scripts
#
	install -d ${D}${sysconfdir}/init.d
	install -d ${D}${sysconfdir}/rcS.d
	install -d ${D}${sysconfdir}/rc0.d
	install -d ${D}${sysconfdir}/rc1.d
	install -d ${D}${sysconfdir}/rc2.d
	install -d ${D}${sysconfdir}/rc3.d
	install -d ${D}${sysconfdir}/rc4.d
	install -d ${D}${sysconfdir}/rc5.d
	install -d ${D}${sysconfdir}/rc6.d
	install -d ${D}${sysconfdir}/default
	install -d ${D}${sysconfdir}/default/volatiles

	install -m 0755    ${WORKDIR}/functions		${D}${sysconfdir}/init.d
	install -m 0755    ${WORKDIR}/bootmisc.sh	${D}${sysconfdir}/init.d
	install -m 0755    ${WORKDIR}/checkroot		${D}${sysconfdir}/init.d
	install -m 0755    ${WORKDIR}/finish.sh		${D}${sysconfdir}/init.d
	install -m 0755    ${WORKDIR}/halt		${D}${sysconfdir}/init.d
	install -m 0755    ${WORKDIR}/hostname.sh	${D}${sysconfdir}/init.d
	install -m 0755    ${WORKDIR}/mountall.sh	${D}${sysconfdir}/init.d
	install -m 0755    ${WORKDIR}/mountnfs.sh	${D}${sysconfdir}/init.d
	install -m 0755    ${WORKDIR}/ramdisk		${D}${sysconfdir}/init.d
	install -m 0755    ${WORKDIR}/reboot		${D}${sysconfdir}/init.d
	install -m 0755    ${WORKDIR}/rmnologin	${D}${sysconfdir}/init.d
	install -m 0755    ${WORKDIR}/sendsigs		${D}${sysconfdir}/init.d
	install -m 0755    ${WORKDIR}/single		${D}${sysconfdir}/init.d
	install -m 0755    ${WORKDIR}/umountnfs.sh	${D}${sysconfdir}/init.d
	install -m 0755    ${WORKDIR}/urandom		${D}${sysconfdir}/init.d
	install -m 0755    ${WORKDIR}/devpts.sh	${D}${sysconfdir}/init.d
	install -m 0755    ${WORKDIR}/devpts		${D}${sysconfdir}/default
	install -m 0755    ${WORKDIR}/sysfs.sh		${D}${sysconfdir}/init.d
	install -m 0755    ${WORKDIR}/populate-volatile.sh ${D}${sysconfdir}/init.d
	install -m 0755    ${WORKDIR}/save-rtc.sh	${D}${sysconfdir}/init.d
	install -m 0644    ${WORKDIR}/volatiles		${D}${sysconfdir}/default/volatiles/00_core
	if [ "${TARGET_ARCH}" = "arm" ]; then
		install -m 0755 ${WORKDIR}/alignment.sh	${D}${sysconfdir}/init.d
	fi
	install -m 0755    ${WORKDIR}/rootfs-ro		${D}${sysconfdir}/init.d
	install -m 0755    ${WORKDIR}/rootfs-rw		${D}${sysconfdir}/init.d
	install -m 0755    ${WORKDIR}/pre-configure	${D}${sysconfdir}/init.d
	install -m 0755    ${WORKDIR}/post-configure	${D}${sysconfdir}/init.d
#
# Install device dependent scripts
#
	install -m 0755 ${WORKDIR}/banner	${D}${sysconfdir}/init.d/banner
	install -m 0755 ${WORKDIR}/devices	${D}${sysconfdir}/init.d/devices
	install -m 0755 ${WORKDIR}/umountfs	${D}${sysconfdir}/init.d/umountfs
#
# Create runlevel links
#
	ln -sf		../init.d/rmnologin	${D}${sysconfdir}/rc2.d/S99rmnologin
	ln -sf		../init.d/rmnologin	${D}${sysconfdir}/rc3.d/S99rmnologin
	ln -sf		../init.d/rmnologin	${D}${sysconfdir}/rc4.d/S99rmnologin
	ln -sf		../init.d/rmnologin	${D}${sysconfdir}/rc5.d/S99rmnologin
	ln -sf		../init.d/sendsigs	${D}${sysconfdir}/rc6.d/S20sendsigs
#	ln -sf		../init.d/urandom	${D}${sysconfdir}/rc6.d/S30urandom
	ln -sf		../init.d/umountnfs.sh	${D}${sysconfdir}/rc6.d/S31umountnfs.sh
	ln -sf		../init.d/umountfs	${D}${sysconfdir}/rc6.d/S40umountfs
	# udev will run at S55 if installed
	ln -sf          ../init.d/ramdisk       ${D}${sysconfdir}/rcS.d/S30ramdisk
	ln -sf		../init.d/reboot	${D}${sysconfdir}/rc6.d/S90reboot
	ln -sf		../init.d/sendsigs	${D}${sysconfdir}/rc0.d/S20sendsigs
#	ln -sf		../init.d/urandom	${D}${sysconfdir}/rc0.d/S30urandom
	ln -sf		../init.d/umountnfs.sh	${D}${sysconfdir}/rc0.d/S31umountnfs.sh
	ln -sf		../init.d/umountfs	${D}${sysconfdir}/rc0.d/S40umountfs
	# udev will run at S55 if installed
	ln -sf		../init.d/halt		${D}${sysconfdir}/rc0.d/S90halt
	ln -sf		../init.d/save-rtc.sh	${D}${sysconfdir}/rc0.d/S25save-rtc.sh
	ln -sf		../init.d/save-rtc.sh	${D}${sysconfdir}/rc6.d/S25save-rtc.sh
	ln -sf		../init.d/banner	${D}${sysconfdir}/rcS.d/S02banner
	ln -sf		../init.d/checkroot		${D}${sysconfdir}/rcS.d/S10checkroot
#	ln -sf		../init.d/checkfs.sh	${D}${sysconfdir}/rcS.d/S30checkfs.sh
	ln -sf		../init.d/mountall.sh	${D}${sysconfdir}/rcS.d/S35mountall.sh
	ln -sf		../init.d/hostname.sh	${D}${sysconfdir}/rcS.d/S39hostname.sh
	ln -sf		../init.d/mountnfs.sh	${D}${sysconfdir}/rcS.d/S45mountnfs.sh
	ln -sf		../init.d/bootmisc.sh	${D}${sysconfdir}/rcS.d/S55bootmisc.sh
#	ln -sf		../init.d/urandom	${D}${sysconfdir}/rcS.d/S55urandom
	ln -sf		../init.d/finish.sh	${D}${sysconfdir}/rcS.d/S99finish.sh
	ln -sf		../init.d/devices	${D}${sysconfdir}/rcS.d/S05devices
	# udev will run at S04 if installed
	ln -sf		../init.d/sysfs.sh	${D}${sysconfdir}/rcS.d/S03sysfs
	ln -sf		../init.d/populate-volatile.sh	${D}${sysconfdir}/rcS.d/S37populate-volatile.sh
	ln -sf		../init.d/devpts.sh	${D}${sysconfdir}/rcS.d/S38devpts.sh
	if [ "${TARGET_ARCH}" = "arm" ]; then
		ln -sf	../init.d/alignment.sh	${D}${sysconfdir}/rcS.d/S06alignment
	fi

	# Handle first boot tasks
	ln -sf		../init.d/pre-configure	${D}${sysconfdir}/rcS.d/S02pre-configure
	ln -sf		../init.d/post-configure	${D}${sysconfdir}/rcS.d/S99post-configure

	install -m 0755		${WORKDIR}/device_table.txt		${D}${sysconfdir}/device_table
}

# Angstrom doesn't support devfs
do_install_append_angstrom () {
	rm ${D}${sysconfdir}/init.d/devices ${D}${sysconfdir}/rcS.d/S05devices
}

# HIPOX needs /sys in reboot for kexec check
do_install_append_hipox () {
	ln -sf		../init.d/sysfs.sh	${D}${sysconfdir}/rc6.d/S80sysfs
}

# uclibc's date does support only SUSv3 strptime()
do_install_append_libc-uclibc() {
	install -m 0755    ${WORKDIR}/save-rtc-uclibc.sh ${D}${sysconfdir}/init.d/save-rtc.sh
}
