﻿ALTER TABLE [rdr].[LoadEvent]
    ADD CONSTRAINT [FK_LoadEvent_Event] FOREIGN KEY ([EventId]) REFERENCES [rdr].[Event] ([EventId]) ON DELETE NO ACTION ON UPDATE NO ACTION;

