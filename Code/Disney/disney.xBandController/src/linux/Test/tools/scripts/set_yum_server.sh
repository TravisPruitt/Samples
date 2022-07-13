TARGET_HOST=10.110.1.204

  grep -q xbrcyumserver /etc/hosts
  if [ $? -eq "0" ]; then
      echo "xbrcyumserver is already defined for host $TARGET_HOST"
  else
      echo "xbrcyumserver is not defined for host $TARGET_HOST. Adding now..."
      ssh root@$TARGET_HOST echo "10.110.1.247   xbrcyumserver" >> /etc/hosts
  fi

