#!/bin/bash

for HOST in 10.75.2.42;
#for HOST in 10.110.1.102 10.110.1.114 10.110.1.117 10.110.1.102 10.110.1.103 10.110.1.92 10.110.1.95 10.110.1.111 10.110.1.93 10.110.1.108 10.110.1.112 10.110.1.110 10.110.1.113 10.110.1.120 10.110.1.86 10.110.1.99 10.110.1.146 10.110.1.170 10.110.1.174 10.110.1.239 10.110.1.178 10.110.1.143 10.110.1.205 10.110.1.204 10.110.1.202   10.110.1.203 10.110.1.201 10.110.1.245 10.110.1.237 10.110.1.235 10.110.1.234 10.110.1.206 10.110.1.225 10.110.1.236;
do
    ssh root@${HOST} "chage -M -1 testuser"
done
