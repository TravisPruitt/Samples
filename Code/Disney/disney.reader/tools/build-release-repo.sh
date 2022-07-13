#!/bin/bash
#
# Builds a release package directory
#
# Usage: build-release-repo.sh <target> <output_dir>
#
# Requirements:
#
# This script needs OEBASE to be set to the base build directory.
#
# This script make a couple of assumptions about the supplied target parameter:
#
# 1. A directory under $OEBASE/arago-custom/recipes exists with the
#    target name
#
# 2. That directory contains a deps.inc file that can be sourced
#    and sets the DEPS variable to a list of packages to find
#    and place in the generated release repo.
# 

source tools/.opkg.sh || exit 1

if [ $# != 2 ]; then
    echo "Usage: $0 <target> <output_dir>"
    exit
fi
TARGET=$1
TARGET_DIR=$2

# source in the dependancy list
source $OEBASE/arago-custom/recipes/$TARGET/deps.inc
if [ -z "$DEPS" ]; then
    echo "DEPS not defined in $OEBASE/$TARGET/deps.inc"
    exit 1
fi

test -d $TARGET_DIR && rm -fr $TARGET_DIR
mkdir -p $TARGET_DIR

# Copy source packages to destination
for pkg in  $DEPS $TARGET; do
    find $SOURCE_DIR -name *${pkg}*.ipk -exec cp '{}' $TARGET_DIR \;
done

echo "Generating new repository index at $TARGET_DIR"
opkg-make-index -p $TARGET_DIR/Packages -l $TARGET_DIR/Packages.filelist $TARGET_DIR > /dev/null

echo "done."
