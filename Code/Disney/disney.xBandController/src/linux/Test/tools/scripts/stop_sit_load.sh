#!/bin/bash

if [ "$(ps -eaf | grep node | grep sit)" ]
then
  
  ps -eaf | grep node | grep sit | awk '{print $2}' | xargs kill -9 
  if [ "$?" -eq "0" ]; then
    echo "SIT Load test stopped"
  fi
fi
