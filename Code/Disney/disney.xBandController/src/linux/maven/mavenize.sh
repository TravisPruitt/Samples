#
# !/bin/bash
#


PWD=`pwd`
PROJECT=`basename "$PWD"`
echo "Project: " $PROJECT

# move pom file
if [ ! -f pom.xml ]; then
    git mv maven/pom.xml pom.xml
	cp -f maven/.gitignore .gitignore
	git rm -r maven
	rm -rf maven
fi

# move top level source directories into src/main/java
function movesources()
{
	# create destination
	if [ ! -d src/main/java ]; then
		mkdir -p src/main/java > /dev/null 2>&1 
	fi

	echo ">> move top level source directories"

	# iterate through files in from/*
	for f in src/*
	do
		basef=`basename $f`

		# skip any maven directories
		if [ "$basef" != "main" ]; then
			git mv "src/$basef" "src/main/java/$basef"
		fi
	done
}

# move main resource (non-test) files
function movemainresources()
{
	local from=$1
	local to=$2

	echo ">> move main resources $from $to"

	# iterate through files in from/*
	for f in $from/*
	do
		basef=`basename $f`
		extf="${basef##*.}"
		# echo "Processing: $basef"
		if [ -d "$f" ]; then
			# skip bvt directories
			if [ "$basef" != "junit" ]; then
				# recurse
				movemainresources "$f" "$to/$basef"
			fi
		else
			if [ "$extf" != "java" ]; then
				if [ ! -d "$to" ]; then
					mkdir -p "$to" > /dev/null 2>&1
				fi
				git mv $PWD/"$f" "$to/$basef"
			fi
		fi
	done
}

# move java test source files
function movetestsources()
{
	local from=$1
	local to=$2
	local copyflag=$3

	echo ">> move test sources $from $to"

	# iterate through files in from/*
	for f in $from/*
	do
		basef=`basename $f`
		extf="${basef##*.}"
		# echo "Processing: $basef"
		if [ -d "$f" ]; then
			if [ "$basef" = "junit" ]; then
				movetestsources "$f" "$to/$basef" Y
			else
				movetestsources "$f" "$to/$basef" $copyflag
			fi
		else
			if [ "$copyflag" = "Y" ]; then
				if [ "$extf" = "java" ]; then
					if [ ! -d "$to" ]; then
						mkdir -p "$to" > /dev/null 2>&1
					fi
					git mv "$from/$basef" "$to/$basef"
				fi
			fi
		fi
	done
}

# move test resource files
function movetestresources()
{
	local from=$1
	local to=$2
	local copyflag=$3

	echo ">> move test resources $from $to"

	# iterate through files in from/*
	for f in $from/*
	do
		basef=`basename $f`
		extf="${basef##*.}"
		# echo "Processing: $basef"
		if [ -d "$f" ]; then
			if [ "$basef" = "junit" ]; then
				movetestresources "$f" "$to/$basef" Y
			else
				movetestresources "$f" "$to/$basef" $copyflag
			fi
		else
			if [ "$copyflag" = "Y" ]; then
				if [ "$extf" != "java" ]; then
					if [ ! -d "$to" ]; then
						mkdir -p "$to" > /dev/null 2>&1
					fi
					git mv "$from/$basef" "$to/$basef"
				fi
			fi
		fi
	done
}


#echo Processing

# first, move all sources from src into maven/src/main/java
movesources
movemainresources "src/main/java" "src/main/resources"
movetestsources "src/main/java" "src/test/java" N
movetestresources "src/main/java" "src/test/resources" N

