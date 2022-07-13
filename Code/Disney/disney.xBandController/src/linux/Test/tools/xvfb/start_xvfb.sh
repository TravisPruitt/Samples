PORT=99

if [ "$(pidof Xvfb)" ]
then
  echo "Xvfb was already running, so killing before restarting..."
  kill -9 `pidof Xvfb`
fi

#. xvfb_hlpr.sh $PORT
export DISPLAY=:$PORT
echo "Starting Xvfb on port $PORT"
nohup /usr/bin/Xvfb :$PORT -ac -screen 0 1024x768x8 >/dev/null 2>&1 &
