#
#!/bin/bash
#
# describe image
#
# get version information for each software package
# and store that info in files in the /VERSION of the filesystem.
#
# USAGE version.sh (run in 

here=$(cd "$(dirname "$0")"; pwd)
dest=$1/VERSION
shift

#USAGE: git2file <software name> <directory of git repo>
git2file (){
	dir=$2
	pushd $dir > /dev/null
	version=$(git describe --tag --dirty 2>/dev/null)
	numberlogs=$(echo $version | grep -o "[0-9]*-g" | grep -o "[0-9]*")
	isdirty=$(echo $version | grep -o "dirty")
	
	filename=$dest/$1_=_$version.ver
	
	echo "$1 = $version" 				>  $filename
	echo 								>> $filename
	echo $(git log -n 1) 				>> $filename
	echo 								>> $filename

	if [ "$isdirty" == "dirty" ]; then 
		echo "DIRTY (BUILD != REPO)" 	>> $filename
		echo $(git diff --stat -- .)	>> $filename
	fi

	if [ $(( $numberlogs )) ]; then  
		echo $(git log -n $numberlogs --oneline) >> $filename
	fi
	
	popd > /dev/null
	echo "See $filename for release notes / logs"
}

echo "--------------------------------------"

#make the version directory
if [ -d $dest ]; then rm -r $dest; fi
mkdir $dest

# BUILT BY
iam=$(whoami)
echo "iam   = $iam" > $dest/BY-$iam

# BUILD DATE
echo "timedate = $timedate" > $dest/BUILT-ON-$(date +"%y-%m-%d_%H:%M:%S")

# APPLICATION(S)
for app in $@; do
	git2file $app ${OEBASE}/.
done

# FILESYSTEM
git2file Filesystem-synapse ${OEBASE}/arago
git2file bitbake			${OEBASE}/arago-bitbake
git2file Filesystem-apps	${OEBASE}/arago-downloads
git2file Filesystem-core	${OEBASE}/arago-oe-dev

echo "======================================"
echo "Done!"
