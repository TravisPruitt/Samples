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
update Status set Value='-1' where Property='LastMessageIdToPostStream';
update Status set Value='-1' where Property='LastMessageIdToJMS';
delete from Config where class='ControllerInfo' and property='model';
insert into Config (class,property,value) values ('ControllerInfo', 'model', 'com.disney.xband.xbrc.spacemodel.CEP');

