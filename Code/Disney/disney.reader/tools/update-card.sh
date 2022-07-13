#!/bin/bash
#
# update-card.sh <distribution directory>
#
# Code is specific to a 2G micro-SD card.
# - Performs a low-level format, specific to bootloader
# - Performs a filesystem format
# - Copies distribution files to the SD card
#

# Re-run w/ sudo if not already root
if ! [[ `id` =~ uid=0\(root\) ]]; then
    exec sudo $0 $@
fi

if [ ! -d "$1" ]; then
	echo "Error, bad distribution directory argument"
	exit 1
fi

if [ ! -f "$1/MLO" ]; then
	echo "Error, bad distribution directory argument"
	exit 1
fi

echo "**********************************************"
echo " PREPARING TO PROGRAM AN SD CARD"
echo "**********************************************"
pause 1

DIST=$(readlink -f $1)
CYLINDERS=230
MIN_SZ=1900000000
MAX_SZ=2100000000

# LOOK FOR A DEVICE THAT IS REMOVABLE AND CORRECT SIZE, 
# ASSUME THAT IT IS THE CORRECT ON :)
SD_OK() {
	removable=$(cat /sys/class/block/$(basename $1)/removable)
	if [ ! "1" = "$removable" ]; then
		echo "$1 is not a removable device"
		return 1
	fi

	size=$(fdisk -l $1 | awk '/^Disk.*bytes$/ {print $5}')

	if [ $(($size + 0)) -lt $MIN_SZ ]; then
		echo "Disk $1 is too small ($(($size + 0)) bytes)"
		return 1
	fi

	if [ $(($size + 0)) -gt $MAX_SZ ]; then
		echo "Disk $1 is suspiciously large ($(($size + 0)) bytes)"
		return 1
	fi
}
if   SD_OK /dev/sdb; then DEV=/dev/sdb
elif SD_OK /dev/sdc; then DEV=/dev/sdc 
elif SD_OK /dev/sdd; then DEV=/dev/sdd 
elif SD_OK /dev/sde; then DEV=/dev/sde 
else
	echo "Did not find an appropriate device to program."
	exit 1
fi


echo "**********************************************"
echo " USING $DEV"
echo "**********************************************"

# now we do the real operation

umount "$DEV"1
umount "$DEV"2
umount "$DEV"3

{
echo ,230,0x0C,*
#echo 9,220,,-

} | sfdisk -D -H 255 -S 63 -C $CYLINDERS $DEV

# now format the partition
mkfs.vfat -F 32 "$DEV"1 -n SD-BOOT

if [ ! -d /media/SD-BOOT ]; then
	mkdir /media/SD-BOOT
fi

mount "$DEV"1 /media/SD-BOOT

cp $DIST/MLO		/media/SD-BOOT/
cp $DIST/u-boot.*       /media/SD-BOOT/
cp $DIST/uImage         /media/SD-BOOT/
if [ -e $DIST/filesys-image.ubi ]; then
    cp $DIST/filesys-image.ubi /media/SD-BOOT/rootfs.ubi
else
    cp $DIST/rootfs.ubi /media/SD-BOOT/rootfs.ubi
fi
if [ -e $DIST/boot.scr ]; then
    cp $DIST/boot.scr /media/SD-BOOT/
fi
if [ -e $DIST/uEnv.txt ]; then
    cp $DIST/uEnv.txt /media/SD-BOOT/
fi

sync

version=$(echo $1  | sed 's|/$||' | sed 's|.*[//]||')
echo "VERSION = $version"

echo "unmounting. Please wait 30 seconds or so..."
umount "$DEV"1
rm -rf /media/SD-BOOT

echo "DONE! Remove SD card. You can re-install it to automatically remount."
