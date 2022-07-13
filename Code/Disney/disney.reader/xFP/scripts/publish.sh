#!/bin/bash

if [ ! -d "distro" ]; then
    mkdir distro
fi

cp out/* distro
cp target-scripts/* distro
cp target-files/*.wav distro
cp target-files/config.json distro
cp target-files/root_profile distro
cp target-files/V300D.ldr distro
