use Mayhem;

delete from Messages;
delete from GuestPosition;
delete from GST;
update Status set Value='-1' where Property='LastMessageIdToPostStream';
update Status set Value='-1' where Property='LastMessageIdToJMS';
update Status set Value='1970-01-01T00:00:00.000' where Property='LastStateStore';
delete from Config where class='ControllerInfo' and property='model';
insert into Config (class,property,value) values ('ControllerInfo', 'model', 'com.disney.xband.xbrc.spacemodel.CEP');

