ALTER TABLE [gxp].[BusinessEvent]
    ADD CONSTRAINT [FK_BusinessEvent_EventLocation] FOREIGN KEY ([EventLocationID]) REFERENCES [gxp].[EventLocation] ([EventLocationID]) ON DELETE NO ACTION ON UPDATE NO ACTION;

