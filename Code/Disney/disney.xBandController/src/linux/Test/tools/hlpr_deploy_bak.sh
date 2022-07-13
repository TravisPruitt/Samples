USER=$1
IP=$2
PACKAGE=$3
BUILD=$4

#Update the yum repository
ssh $USER@$IP yum clean all

YUM_STRING=`ssh $USER@$IP yum list | grep $PACKAGE | grep installed` 

if [ ! -z $BUILD ]; then
  #User specified a specific build, so determine if installed build is newer or older than this build
  INSTALLED=`ssh $USER@$IP yum list | grep $PACKAGE | grep installed | awk '{print $2}' | awk -F "-" '{print $2}'`
  MAJ_VER=`ssh $USER@$IP yum list | grep $PACKAGE | grep installed | awk '{print $2}' | awk -F "-" '{print $1}'`
  #echo $INSTALLED > current_build

  #Installing a version older than the current, so using'downgrade' 
  if [[ $INSTALLED -gt $BUILD ]]; then
    #Installing a version older than the current version
    ssh $USER@$IP yum -y downgrade $PACKAGE-$MAJ_VER-$BUILD
  else
      #Installing a version newer than the current, so using 'install'
      ssh $USER@$IP yum -y install $PACKAGE-$MAJ_VER-$BUILD
  fi
else
  #No build specified, installing the latest 
  ssh $USER@$IP yum -y install $PACKAGE
fi

#Write out the current build number 
ssh $USER@$IP yum list | grep $PACKAGE | grep installed | awk '{print $2}' | awk -F "-" '{print $2}' > current_build 

#Now restart tomcat and swedish chef services
SERVICE_NAME=`echo $YUM_STRING | awk -F "-" '{print $1}'` 
ssh $USER@$IP /etc/init.d/$SERVICE_NAME start
ssh $USER@$IP /etc/init.d/bootssptcserver1 restart
