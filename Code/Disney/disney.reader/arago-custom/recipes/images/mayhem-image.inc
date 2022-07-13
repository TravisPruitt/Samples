# Common pieces for Mayhem images

ROOTFS_SIZE = "131072"

# Command for creating UBI image
# We have two partitions
# /  128MiB (RO)
# /var	    rest (RW)
IMAGE_CMD_ubi_overo = " \
echo \[ubifs\] > ubinize.cfg ; \
echo mode=ubi >> ubinize.cfg ; \
echo vol_id=0 >> ubinize.cfg ; \
echo vol_type=dynamic >> ubinize.cfg ; \
echo vol_name=rootfs >> ubinize.cfg ; \
echo vol_size=128MiB >> ubinize.cfg; \
echo image=${DEPLOY_DIR_IMAGE}/${IMAGE_NAME}.ubifs.img >> ubinize.cfg ; \
# \
echo \[ubifs2\] >> ubinize.cfg ; \
echo mode=ubi >> ubinize.cfg ; \
echo vol_id=1 >> ubinize.cfg ; \
echo vol_type=dynamic >> ubinize.cfg ; \
echo vol_name=var >> ubinize.cfg ; \
echo vol_flags=autoresize >> ubinize.cfg; \
echo image=${DEPLOY_DIR_IMAGE}/var.ubifs.img >> ubinize.cfg ; \
# \
mkfs.ubifs -r ${IMAGE_ROOTFS}/var -o ${DEPLOY_DIR_IMAGE}/var.ubifs.img ${MKUBIFS_ARGS} && \
mkfs.ubifs -r ${IMAGE_ROOTFS} -o ${DEPLOY_DIR_IMAGE}/${IMAGE_NAME}.ubifs.img ${MKUBIFS_ARGS} && \
ubinize -o ${DEPLOY_DIR_IMAGE}/${IMAGE_NAME}.rootfs.ubi ${UBINIZE_ARGS} ubinize.cfg"

IMAGE_CMD_ubi_xbr4 = " \
echo \[ubifs\] > ubinize.cfg ; \
echo mode=ubi >> ubinize.cfg ; \
echo vol_id=0 >> ubinize.cfg ; \
echo vol_type=dynamic >> ubinize.cfg ; \
echo vol_name=rootfs >> ubinize.cfg ; \
echo vol_size=128MiB >> ubinize.cfg; \
echo image=${DEPLOY_DIR_IMAGE}/${IMAGE_NAME}.ubifs.img >> ubinize.cfg ; \
# \
echo \[ubifs2\] >> ubinize.cfg ; \
echo mode=ubi >> ubinize.cfg ; \
echo vol_id=1 >> ubinize.cfg ; \
echo vol_type=dynamic >> ubinize.cfg ; \
echo vol_name=var >> ubinize.cfg ; \
echo vol_flags=autoresize >> ubinize.cfg; \
echo image=${DEPLOY_DIR_IMAGE}/var.ubifs.img >> ubinize.cfg ; \
# \
mkfs.ubifs -r ${IMAGE_ROOTFS}/var -o ${DEPLOY_DIR_IMAGE}/var.ubifs.img ${MKUBIFS_ARGS} && \
mkfs.ubifs -r ${IMAGE_ROOTFS} -o ${DEPLOY_DIR_IMAGE}/${IMAGE_NAME}.ubifs.img ${MKUBIFS_ARGS} && \
ubinize -o ${DEPLOY_DIR_IMAGE}/${IMAGE_NAME}.rootfs.ubi ${UBINIZE_ARGS} ubinize.cfg"

# 4KB per 1 inode should be enough
EXTRA_IMAGECMD_ext2.gz += "-i 4096"

# Disable this due to distribution restrictions
# DISTRO_SSH_DAEMON ?= "dropbear"

IMAGE_PREPROCESS_COMMAND = "create_etc_timestamp"

IMAGE_INSTALL = "\
    ${DISTRO_SSH_DAEMON} \
    angstrom-version \
    "

IMAGE_LINGUAS = ""

inherit image
