dd-removable if=mbr of=\\?\Device\Harddisk1\Partition0
dd-removable if=boot.img seek=63 bs=512 of=\\?\Device\Harddisk1\Partition0
dd-removable if=ext2.img seek=4131 bs=17920 of=\\?\Device\Harddisk1\Partition0
