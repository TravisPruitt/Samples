#!/bin/sh

if ! test -e /etc/.configured; then
	> /etc/.configured

    # Make sure we sync write buffers on first boot
    sync
fi
