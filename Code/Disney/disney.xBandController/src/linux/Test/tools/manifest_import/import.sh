#/bin/bash
RAW_MANIFEST=$1

IDMS_HOST=10.110.1.103
IDMS_PORT=8090

URL="http://$IDMS_HOST:$IDMS_PORT/IDMS/import/file"
CONTENT_TYPE="application/text"
MANIFEST_FILE="./ngeXBands.txt"

clear

if [ "$#" -gt "0" ]; then
  ./sanitize_manifest.sh $RAW_MANIFEST
fi

echo "Posting to IDMS on host $IDMS_HOST"
echo $URL
curl -v -X POST -H $CONTENT_TYPE -T $MANIFEST_FILE $URL
echo

