# starts a new simulation

# reset the database
cd ../Database/Common
./rebuilddatabase.sh SpaceModel

# start the Controller
cd ../../Controller
java -jar xbrc.jar &
sleep 3

# start the readers
cd ../Reader
./startreaders.sh SpaceModel

# start the Guests
cd ../SpaceModelSimulator
Debug/SpaceModelSimulator SpaceModelConfig.xml &

