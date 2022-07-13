#!/bin/bash

USEIP=$1

echo "Using IP: $USEIP"

scp tcinstall.tar root@$USEIP:/tmp
testuser@TST-Selenium:~/tcinstall$ ssh root@$USEIP 'cd /tmp;tar -xvf tcinstall.tar;cd tcinstall;./t2.sh'

