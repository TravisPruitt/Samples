﻿ALTER TABLE [rdr].[ExitEvent]
    ADD CONSTRAINT [FK_ExitEvent_Event] FOREIGN KEY ([EventId]) REFERENCES [rdr].[Event] ([EventId]) ON DELETE NO ACTION ON UPDATE NO ACTION;

