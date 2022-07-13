HOST=$1
ssh root@$HOST "/etc/init.d/tomcat6 stop"
ssh root@$HOST "yum -y erase tomcat6.noarch"   
ssh root@$HOST "yum -y erase tomcat6-admin-webapps.noarch"
ssh root@$HOST "yum -y erase tomcat6-el-1.0-api.noarch"
ssh root@$HOST "yum -y erase tomcat6-jsp-2.1-api.noarch"   
ssh root@$HOST "yum -y erase tomcat6-lib.noarch"
ssh root@$HOST "yum -y erase tomcat6-servlet-2.5-api.noarch"
ssh root@$HOST "yum -y erase tomcat6-webapps.noarch"

