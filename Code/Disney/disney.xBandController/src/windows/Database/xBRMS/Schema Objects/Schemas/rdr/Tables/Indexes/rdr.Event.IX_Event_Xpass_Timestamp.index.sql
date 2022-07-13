﻿CREATE NONCLUSTERED INDEX [IX_Event_Xpass_Timestamp] ON [rdr].[Event] 
(
	[xPass] ASC,
	[Timestamp] ASC
)
INCLUDE ( [GuestID],
[RideNumber],
[FacilityID],
[EventTypeID],
[BandTypeID]) WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, SORT_IN_TEMPDB = OFF, IGNORE_DUP_KEY = OFF, DROP_EXISTING = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]


