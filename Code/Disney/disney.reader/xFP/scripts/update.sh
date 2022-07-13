#!/bin/sh
# Upgrade all of the files on an xFP
#
ssh root@$1 "/etc/init.d/dap-reader stop"
scp out/* root@$1:/mayhem
scp target-scripts/* root@$1:/mayhem
scp target-files/*.wav root@$1:/mayhem
scp target-files/config.json root@$1:/mayhem
scp target-files/root_profile root@$1:/home/root/.profile
scp target-files/V300D.ldr root@$1:/mayhem
ssh root@$1 "sync"
ssh root@$1 "/etc/init.d/dap-reader start"
