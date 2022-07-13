use Mayhem;

delete from Messages;
delete from GuestPosition;
delete from GST;
update Status set Value='-1' where Property='LastMessageIdToPostStream';
update Status set Value='-1' where Property='LastMessageIdToJMS';
update Status set Value='1970-01-01T00:00:00.000' where Property='LastStateStore';
update Config set value='com.disney.xband.xbrc.attractionmodel.CEP' where property='model';

