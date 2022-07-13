#/bin/bash

BENCH=$1
BUILD_NUM=$2
USER=root
PWD=`pwd`
BUILD_SERVER=10.75.3.41
YUM_SERVER=10.110.1.65
#PUSH=0
PUSH=1

if [ $# -lt 1 ]; then
  echo "USAGE: deploy_build.sh {bench} [{build_num}]"
  echo "    currently supported benches: bvt sit int"
  exit
fi

if [ "$BENCH" = "bvt" ]; then
  YUM_SERVER=10.110.1.65
elif [ "$BENCH" = "sit" ]; then
  #Yum repository on SIT XBRMS isn't up yet, so use BVT bench for now  
  #YUM_SERVER=10.110.1.102
  YUM_SERVER=10.110.1.65
elif [ "$BENCH" = "int" ]; then
  YUM_SERVER=10.110.1.247
else
  echo "$BENCH is not supported"
  #exit 1
fi

if [ "$PUSH" -eq "1" ]; then
  #Push out the build from the build server to the yum server
  if [ ! -z $BUILD_NUM ]; then
    #Build number was specified, push that specific build out
    echo "Pushing build $BUILD_NUM from the build machine to the yum server..."
    ssh root@$BUILD_SERVER /builds/disney.xBand/pushtoyum.sh $YUM_SERVER $BUILD_NUM
  else
    #No build specified, push the most currrent build out
    echo "Pushing the latest build from the build machine to the yum server..."
    ssh root@$BUILD_SERVER /builds/disney.xBand/pushtoyum.sh $YUM_SERVER
  fi
fi

while read line; do
  IP=`echo $line | awk '{print $1}'`
  PACKAGE=`echo $line | awk '{print $2}'`
  #echo "$IP  $PACKAGE"
  if [ ! -z $BUILD_NUM ]; then
    echo "Installing $PACKAGE-$BUILD_NUM package on $IP..."
  else
    echo "Installing $PACKAGE package on $IP..."  
  fi
. ./hlpr_deploy.sh $USER $IP $PACKAGE $BUILD_NUM >$PWD/logs/deploy_to_$IP.out 2>&1 &
done < benches/$BENCH

