MANIFEST=$1
SANITIZED_MANIFEST=./ngeXBands.txt
IN_HEADER=1

#Clear the manifest
echo -n > $SANITIZED_MANIFEST

#Check to see if the manifset has a header
grep -q TagID $MANIFEST
if [ $? -ne 0 ]; then
  #No header, so set to false
  IN_HEADER=0
fi


while read line; do

  if [ "$IN_HEADER" -eq "0" ]; then
    echo $line >> $SANITIZED_MANIFEST
  fi

  echo $line | grep -q TagID
  if [ $? -eq 0 ]; then
    #we're on the second line, so set IN_HEADER to false
    IN_HEADER=0
  fi

done < $MANIFEST
