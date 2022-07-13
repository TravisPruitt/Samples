DESCRIPTION = "Netplug is a daemon for managing network interface state depending on link status"
SECTION = "base"

inherit update-rc.d

PR = "r1"

SRC_URI = " \
	http://www.red-bean.com/~bos/netplug/netplug-1.2.9.2.tar.bz2 \
	file://netplugd \
"

INITSCRIPT_NAME = "netplugd"
INITSCRIPT_PARAMS = "start 99 S . stop 03 0 6 ."

do_compile () {
	unset prefix
	unset CFLAGS
	oe_runmake
}

do_install () {
	unset prefix
	oe_runmake DESTDIR=${D} initdir=/etc/init.d install
	install -d ${D}${sysconfdir}/init.d
	install -m 0755 ${WORKDIR}/netplugd ${D}${sysconfdir}/init.d
	install -d ${D}/sbin/
	ln -s /bin/ip ${D}/sbin/ip
}

SRC_URI[md5sum] = "1d6db99536bdf875ce441f2c0e45ebf2"
