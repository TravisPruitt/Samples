#
#!/bin/bash
#
# .card.sh <distribution directory>
#
# Code is specific to a 2G micro-SD card.
# - Performs a low-level format, specific to bootloader
# - Performs a filesystem format
# - Copies distribution files to the SD card
#
echo "**********************************************"
echo " PREPARING TO PROGRAM AN SD CARD"
echo "**********************************************"
pause 1

DIST=$1
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
elif SD_OK /dev/sdf; then DEV=/dev/sdf 
elif SD_OK /dev/sdg; then DEV=/dev/sdg
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
echo ,9,0x0C,*
echo 9,220,,-

} | sfdisk -D -H 255 -S 63 -C $CYLINDERS $DEV

# now format the partitions
mkfs.vfat -F 32 "$DEV"1 -n SD-BOOT
mkfs.ext3 $(echo "$DEV"2) -L SD-FSYS

if [ ! -d /media/SD-BOOT ]; then
	mkdir /media/SD-BOOT
fi
if [ ! -d /media/SD-FSYS ]; then
	mkdir /media/SD-FSYS
fi

mount "$DEV"1 /media/SD-BOOT
mount "$DEV"2 /media/SD-FSYS

cp $DIST/MLO		/media/SD-BOOT/
cp $DIST/u-boot.*	/media/SD-BOOT/
cp $DIST/uImage		/media/SD-BOOT/

echo "Writing filesystem to SD card..."
tar xaf $DIST/filesys-image.tar.bz2 -C /media/SD-FSYS

if [ -d $DIST/filesys-overlay ]; then
	echo "copying overlay files onto SD card."
	cp -r $DIST/filesys-overlay/* /media/SD-FSYS/
fi

version=$(echo $1  | sed 's|/$||' | sed 's|.*[//]||')
echo "VERSION = $version"

echo "unmounting. Please wait 30 seconds or so..."
umount "$DEV"1
umount "$DEV"2
rm -rf /media/SD-BOOT /media/SD-FSYS

echo "DONE! Remove SD card. You can re-install it to automatically remount."
