DESCRIPTION = "Linux kernel for Synapse projects"
SECTION = "kernel"
LICENSE = "GPLv2"
KERNEL_IMAGETYPE = "uImage"

inherit kernel

COMPATIBLE_MACHINE = "xbr4"

PV = "3.2.0"
KVER = "3.2.0"

S = "${OEBASE}/xbr4/linux-am33x"

package_stagefile_deploy() {
	if [ "$PSTAGING_ACTIVE" = "1" ]; then
		srcfile=$1
		destfile=$2
		destdir=`dirname $destfile`
		mkdir -p $destdir
		cp -Pp $srcfile $destfile
	fi
}

do_deploy() {

	oenote "KVER                = " ${KVER}
	oenote "KERNEL_VERSION      = " ${KERNEL_VERSION}

	oenote "TMPDIR              = " ${TMPDIR}
	oenote "DEPLOY_DIR          = " ${DEPLOY_DIR}
	oenote "PSTAGE_TMPDIR_STAGE = " ${PSTAGE_TMPDIR_STAGE}
	oenote "WORKDIR             = " ${WORKDIR}
	
	oenote "S                   = " ${S}
	oenote "KERNEL_OUTPUT       = " ${KERNEL_OUTPUT}
	oenote "DEPLOY_DIR_IMAGE    = " ${DEPLOY_DIR_IMAGE}
	oenote "D                   = " ${D}

	IMAGE_RELDIR=`echo ${DEPLOY_DIR_IMAGE} | sed s#${TMPDIR}##`
	W_RELDIR=`echo ${WORKDIR} | sed s#${TMPDIR}##`

	oenote "I_RELDIR       = " ${IMAGE_RELDIR}
	oenote "W_RELDIR       = " ${W_RELDIR}

	install -d ${DEPLOY_DIR_IMAGE}
	install -m 0644 ${KERNEL_OUTPUT} ${DEPLOY_DIR_IMAGE}/${KERNEL_IMAGE_BASE_NAME}.bin

	package_stagefile_deploy ${S}/${KERNEL_OUTPUT} ${PSTAGE_TMPDIR_STAGE}/${W_RELDIR}/${KERNEL_OUTPUT}
	package_stagefile_deploy ${DEPLOY_DIR_IMAGE}/${KERNEL_IMAGE_BASE_NAME}.bin ${PSTAGE_TMPDIR_STAGE}/${IMAGE_RELDIR}/${KERNEL_IMAGE_BASE_NAME}.bin

	if [ -d "${PKGD}/lib" ]; then
		fakeroot tar -cvzf ${DEPLOY_DIR_IMAGE}/${MODULES_IMAGE_BASE_NAME}.tgz -C ${PKGD} lib
		package_stagefile_deploy ${DEPLOY_DIR_IMAGE}/${MODULES_IMAGE_BASE_NAME}.tgz ${PSTAGE_TMPDIR_STAGE}/${IMAGE_RELDIR}/${MODULES_IMAGE_BASE_NAME}.tgz
	fi

	cd ${DEPLOY_DIR_IMAGE}
	rm -f ${KERNEL_IMAGE_SYMLINK_NAME}.bin
	ln -sf ${KERNEL_IMAGE_BASE_NAME}.bin ${KERNEL_IMAGE_SYMLINK_NAME}.bin

	package_stagefile_deploy ${DEPLOY_DIR_IMAGE}/${KERNEL_IMAGE_SYMLINK_NAME}.bin	${PSTAGE_TMPDIR_STAGE}/${IMAGE_RELDIR}/${KERNEL_IMAGE_SYMLINK_NAME}.bin
}

# copy defconfig in place, 
addtask setup_defconfig before do_configure after do_patch
do_setup_defconfig() {
	cp ${S}/arch/arm/configs/xbr4_defconfig ${S}/.config
}
