#!/bin/bash

if [ "$(ps -eaf | grep node | grep bvt)" ]
then
  
  ps -eaf | grep node | grep bvt | awk '{print $2}' | xargs kill -9 
  if [ "$?" -eq "0" ]; then
    echo "BVT Load test stopped"
  fi
fi
