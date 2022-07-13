#!/bin/bash
#Usage: ./start_selenium.sh {node count}
#  where node count represents the number of selenium nodes to launch

DISPLAYPORT=99
NODEPORT=5556
SELENIUM_HOME=/home/testuser

#Check input for num nodes
if [ $# -gt 0 ]
then
  NODECOUNT=$1
else
  NODECOUNT=1
fi

#Clean up any leftover processes that may be running in the background

#Kill selenium hub if it is already running
if [ "$(ps -eaf | grep java | grep selenium | grep "role hub")" ]
then
  echo "Selenium hub is already running, killing it now..."
  ps -eaf | grep java | grep selenium | grep "role hub" | awk '{print $2}' | xargs kill -9 
fi

#Kill any nodes already running
if [ "$(ps -eaf | grep java | grep selenium | grep "role node")" ]
then
  echo "Selenium node is already running, killing it now..."
  ps -eaf | grep java | grep selenium | grep "role node" | awk '{print $2}' | xargs kill -9
fi

#Kill xvfb if it is already running
if [ "$(pidof Xvfb)" ]
then
  echo "Killing Xvfb..."
  kill -9 `pidof Xvfb`
fi

sleep 5 

#Kick off xvfb and selenium processes in the background

#Start Xvfb
export DISPLAY=:$DISPLAYPORT
echo "Starting Xvfb on port $DISPLAYPORT..."
nohup /usr/bin/Xvfb :$DISPLAYPORT -ac -screen 0 1024x768x8 >~/selenium_logs/xvfb.out 2>&1 &
sleep 1

#Start selenium hub
echo "Starting selenium hub..."
java -jar $SELENIUM_HOME/selenium-server-standalone-2.18.0.jar -role hub -sessionMaxIdleTimeInSeconds 25 -timeout 90000 -newSessionWaitTimeout 120000 >~/selenium_logs/hub.out 2>&1 &
sleep 1

#Start selenium node
echo "Starting selenium node on port $NODEPORT..."
java -jar $SELENIUM_HOME/selenium-server-standalone-2.18.0.jar -role node   -hub http://localhost:4444/grid/register -browser browserName=firefox,vemaxItances=5,platform=LINUX -port $NODEPORT -timeout 90000 >~/selenium_logs/node_$NODEPORT.out 2>&1 &
sleep 1
echo "Completed... Please wait a few minutes for the environment to initialize. Literally, like 3 minutes."
