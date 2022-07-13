HOST=$1
ssh root@$HOST "cd /home/testuser; ./installkeystores.sh $HOST testuser 11@disney"
