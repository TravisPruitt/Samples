HOST=$1
 ssh root@$HOST /opt/apps/tcserverApp/tcServer-6.0/tcserver-ctl.sh ssp-tcserver1 stop
 ssh root@$HOST 'sed -i "s/http.port=.*/http.port=8090/" /opt/apps/tcserverApp/tcServer-6.0/ssp-tcserver1/conf/catalina.properties'
 ssh root@$HOST /opt/apps/tcserverApp/tcServer-6.0/tcserver-ctl.sh ssp-tcserver1 start
