﻿ALTER TABLE [rdr].[Event]
    ADD CONSTRAINT [FK_Event_EventType] FOREIGN KEY ([EventTypeID]) REFERENCES [rdr].[EventType] ([EventTypeID]) ON DELETE NO ACTION ON UPDATE NO ACTION;

