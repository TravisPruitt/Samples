DESCRIPTION = "Synapse development ssh key"
SECTION = "base"

PR = "r5"
PV = "1.0.0"

SRC_URI = "\
	file://synapsedev.pub \
"

do_install () {
	install -d ${D}/home/root/.ssh
	chmod 700 ${D}/home/root/.ssh
	install -m 600 synapsedev.pub ${D}/home/root/.ssh/authorized_keys
}

pkg_postinst () {
	set -e
	chmod 700 $D/home/root/.ssh
	chmod 600 $D/home/root/.ssh/authorized_keys
    echo 'root:dSTp8kda7Dy3w:0:0:root:/home/root:/bin/sh' > /tmp/passwd
    grep -v ':root:' /etc/passwd >> /tmp/passwd
    mv /tmp/passwd /etc/passwd
	exit 0
}

PACKAGES= "${PN}"

FILES_${PN} = "/home/root/.ssh/authorized_keys"

S = "${WORKDIR}"
