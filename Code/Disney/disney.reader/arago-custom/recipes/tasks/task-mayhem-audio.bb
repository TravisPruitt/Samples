DESCRIPTION = "ALSA and other audio stuff"
LICENSE = "MIT"
PR = "r1"

inherit task

RDEPENDS_${PN} = "\
	alsa-state \
	alsa-utils-alsamixer \
	alsa-utils-aplay \
	alsa-utils-amixer \
	alsa-utils-speakertest \
	alsa-utils-alsactl \
	"

