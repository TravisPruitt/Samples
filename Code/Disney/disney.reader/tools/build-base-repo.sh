#!/bin/bash
#
# Builds a base package directory
#
# Usage: build-base-repo.sh <output_dir>
#
# Requirements:
#
# This script needs OEBASE to be set to the base build directory.
#

source tools/.opkg.sh || exit 1

EXCLUDE="synapse-grover synapse-dap-reader"

if [ $# != 1 ]; then
    echo "Usage: $0 <output_dir>"
    exit
fi
TARGET_DIR=$1

test -d $TARGET_DIR && rm -fr $TARGET_DIR
mkdir -p $TARGET_DIR

echo "Copying packages to $TARGET_DIR"
cp -r $SOURCE_DIR/* $TARGET_DIR

echo "Cleaning target directory"

# Remove old package indexes
find $TARGET_DIR -name 'Packages*' -type f -delete

# Remove any morgue (old packages) directories
find $TARGET_DIR -depth -name 'morgue' -type d -exec rm -fr '{}' \;

# Determine and delete packages that are included in the release repos
for ex in $EXCLUDE; do
    # source in the dependancy list
    source $OEBASE/arago-custom/recipes/$ex/deps.inc
    if [ -z "$DEPS" ]; then
        echo "DEPS not defined in $OEBASE/$TARGET/deps.inc"
        exit 1
    fi

    for pkg in $DEPS; do
        find $TARGET_DIR -name *${pkg}*.ipk -delete;
    done
done

# Generate the package indexes
cd $TARGET_DIR
DIRS=`find -type d`
for dir in $DIRS; do
    echo "Generating new repository index at $dir"
    opkg-make-index -p $dir/Packages -l $dir/Packages.filelist $dir > /dev/null
done

echo "done."
