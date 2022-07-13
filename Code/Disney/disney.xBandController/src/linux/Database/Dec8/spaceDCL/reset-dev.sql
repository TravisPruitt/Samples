use Mayhem;

delete from Messages;
delete from GuestPosition;
delete from GST;
update Status set Value='-1' where Property='LastMessageIdToPostStream';
update Status set Value='-1' where Property='LastMessageIdToJMS';
delete from Config where class='ControllerInfo' and property='model';
insert into Config (class,property,value) values ('ControllerInfo', 'model', 'com.disney.xband.xbrc.spacemodel.CEP');

