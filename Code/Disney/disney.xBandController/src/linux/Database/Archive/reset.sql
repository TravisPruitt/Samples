use Mayhem;

delete from Messages;
delete from Events;
delete from GuestPosition;
delete from GST;
update Status set Value='-1' where Property='LastIncomingEventId';
update Status set Value='-1' where Property='LastMessageIdToJMS';


