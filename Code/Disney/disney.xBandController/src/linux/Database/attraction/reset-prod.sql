use Mayhem;

delete from Messages;
delete from GuestPosition;
delete from GST;
delete from Reader;
delete from Walls;
delete from KnownBandIDs;
delete from Config;
delete from GridItem;
delete from StoredConfigurations;
delete from Location where name <> 'UNKNOWN';
delete from ImageBlob;
delete from Image;
update Status set Value='-1' where Property='LastMessageIdToPostStream';
update Status set Value='-1' where Property='LastMessageIdToJMS';
update Status set Value='1970-01-01T00:00:00.000' where Property='LastStateStore';
insert into Config (class,property,value) values ('ControllerInfo', 'model', 'com.disney.xband.xbrc.attractionmodel.CEP');

