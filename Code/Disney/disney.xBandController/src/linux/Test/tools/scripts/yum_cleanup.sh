TARGET_HOST=$1

  echo "Backing up the yum repo file on $TARGET_HOST"
  ssh root@$TARGET_HOST cp /etc/yum.repos.d/xbrc.repo ~

    echo "Moving yum plugins on $TARGET_HOST"
    ssh root@$TARGET_HOST mkdir ~/yum-plugins
    ssh root@$TARGET_HOST mv /usr/lib/yum-plugins/rh* ~/yum-plugins

    echo "Cleaning yum repo directory on $TARGET_HOST"
    ssh root@$TARGET_HOST mkdir ~/yum.repos.d
    ssh root@$TARGET_HOST mv -f /etc/yum.repos.d/* ~/yum.repos.d

    #Copy back the xbrc repo file
    echo "Restoring the xbrc repo file on $TARGET_HOST"
    ssh root@$TARGET_HOST cp -f ~/xbrc.repo /etc/yum.repos.d/
