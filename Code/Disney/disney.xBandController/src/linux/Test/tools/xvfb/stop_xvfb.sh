if [ "$(pidof Xvfb)" ]
then
  echo "Killing Xvfb..."
  kill -9 `pidof Xvfb`
fi

echo "Configuring settings..."
export DISPLAY=:0
