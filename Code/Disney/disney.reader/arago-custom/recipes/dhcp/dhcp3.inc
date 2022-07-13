SECTION = "console/network"
DESCRIPTION = "Internet Software Consortium DHCP package"
HOMEPAGE = "http://www.isc.org/"
LICENSE = "BSD"
INC_PR = "r20"

inherit update-rc.d

INITSCRIPT_PACKAGES = "dhcp-server"
INITSCRIPT_NAME_dhcp-server = dhcp-server
INITSCRIPT_PARAMS_dhcp-server = "start 44 2 3 4 5 . stop 44 0 1 6 ."

TARGET_CC_ARCH += "${LDFLAGS}"

do_configure() {
	./configure
}

do_compile() {
	make RANLIB=${RANLIB} PREDEFINES='-D_PATH_DHCPD_DB=\"/var/lib/dhcp/dhcpd.leases\" \
        -D_PATH_DHCLIENT_DB=\"/var/lib/dhcp/dhclient.leases\" \
        -D_PATH_DHCLIENT_SCRIPT=\"/sbin/dhclient-script\" \
        -D_PATH_DHCPD_CONF=\"/etc/dhcp/dhcpd.conf\" \
        -D_PATH_DHCLIENT_CONF=\"/etc/dhcp/dhclient.conf\"'
}

do_install() {
	make -e DESTDIR=${D} USRMANDIR=${mandir}/man1 ADMMANDIR=${mandir}/man8 FFMANDIR=${mandir}/man5 LIBMANDIR=${mandir}/man3 LIBDIR=${libdir} INCDIR=${includedir} install
	install -d ${D}${sysconfdir}/init.d
	install -d ${D}${sysconfdir}/default
	install -d ${D}${sysconfdir}/dhcp
	install -d ${D}${sysconfdir}/dhclient-enter-hooks.d
	install -d ${D}${sysconfdir}/dhclient-exit-hooks.d
	install -m 0755 ${WORKDIR}/init-relay ${D}${sysconfdir}/init.d/dhcp-relay
	install -m 0644 ${WORKDIR}/default-relay ${D}${sysconfdir}/default/dhcp-relay
	install -m 0755 ${WORKDIR}/init-server ${D}${sysconfdir}/init.d/dhcp-server
	install -m 0644 ${WORKDIR}/default-server ${D}${sysconfdir}/default/dhcp-server
	install -m 0644 ${WORKDIR}/dhclient.conf ${D}${sysconfdir}/dhcp/dhclient.conf
	install -m 0644 ${WORKDIR}/dhcpd.conf ${D}${sysconfdir}/dhcp/dhcpd.conf
	install -m 0644 ${WORKDIR}/dhclient-enter-hooks ${D}${sysconfdir}/dhclient-enter-hooks
	install -m 0644 ${WORKDIR}/dhclient-exit-hooks ${D}${sysconfdir}/dhclient-exit-hooks
	install -d ${D}/var/lib/dhcp
	install -m 0755 ${WORKDIR}/flush-leases ${D}${sysconfdir}/dhclient-exit-hooks.d/10flush-leases
	install -m 0755 ${WORKDIR}/dhclient-debug ${D}${sysconfdir}/dhclient-exit-hooks.d/20debug
	install -m 0755 ${WORKDIR}/xbrc ${D}${sysconfdir}/dhclient-exit-hooks.d/52xbrc
	install -m 0755 ${WORKDIR}/reader-restart ${D}${sysconfdir}/dhclient-exit-hooks.d/60reader-restart

	# Symlink resolv.conf to /var/lib/dhcp so changes are not written to the rootfs
	ln -s /var/lib/dhcp/resolv.conf ${D}${sysconfdir}/resolv.conf
}

PACKAGES += "dhcp-server dhcp-client dhcp-relay dhcp-omshell"
FILES_${PN} = ""
FILES_dhcp-server = "${sbindir}/dhcpd ${sysconfdir}/init.d/dhcp-server ${sysconfdir}/default/dhcp-server ${sysconfdir}/dhcp/dhcpd.conf"
FILES_dhcp-relay = "${sbindir}/dhcrelay ${sysconfdir}/init.d/dhcp-relay ${sysconfdir}/default/dhcp-relay"

FILES_dhcp-client = "${base_sbindir}/dhclient ${base_sbindir}/dhclient-script ${sysconfdir}/dhcp/dhclient.conf /var/lib/dhcp \
	${sysconfdir}/dhclient-enter-hooks ${sysconfdir}/dhclient-exit-hooks \
	${sysconfdir}/dhclient-enter-hooks.d ${sysconfdir}/dhclient-exit-hooks.d \
	${sysconfdir}/dhclient-exit-hooks.d/52xbrc \
        ${sysconfdir}/resolv.conf"

FILES_dhcp-omshell = "${bindir}/omshell"

CONFFILES_dhcp-server_nylon = "/etc/dhcp/dhcpd.conf"
CONFFILES_dhcp-relay_nylon = "/etc/default/dhcp-relay"
CONFFILES_dhcp-client_nylon = "/etc/dhcp/dhclient.conf"
