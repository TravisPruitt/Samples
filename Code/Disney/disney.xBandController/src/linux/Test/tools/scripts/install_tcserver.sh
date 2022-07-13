#!/bin/bash

#for USEIP in 10.110.1.205 10.110.1.204 10.110.1.202 10.110.1.203 10.110.1.201 10.110.1.245 10.110.1.237 10.110.1.235 10.110.1.234 10.110.1.206 10.110.1.225 10.110.1.236; do
#for USEIP in 10.110.1.235; do
USEIP=$1

    echo "Using IP: $USEIP"

    echo "scp tcinstall.tar root@$USEIP:/tmp"
    scp files/tcinstall.tar root@$USEIP:/tmp
    echo "ssh root@$USEIP 'cd /tmp;tar -xvf tcinstall.tar;cd tcinstall;./t2.sh'"
    ssh root@$USEIP 'cd /tmp;tar -xvf tcinstall.tar;cd tcinstall;./t2.sh'
