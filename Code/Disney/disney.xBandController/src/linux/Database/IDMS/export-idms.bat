bcp "SELECT * FROM [IDMS_SIT].[dbo].[guest] WHERE [guestId] >= 35001106" queryout dbo.guest.dat -n -E -S %1 -d %2 -U %3 -P %4
bcp "SELECT * FROM [IDMS_SIT].[dbo].[xband] WHERE [xbandId] >= 35001100" queryout dbo.xband.dat -n -E -S %1 -d %2 -U %3 -P %4
bcp dbo.source_system_link out dbo.source_system_link.dat -n -E -S %1 -d %2 -U %3 -P %4
bcp dbo.guest_xband out dbo.guest_xband.dat -n -E -S %1 -d %2 -U %3 -P %4