ALTER TABLE [rdr].[AbandonEvent]
    ADD CONSTRAINT [FK_AbandonEvent_Event] FOREIGN KEY ([EventId]) REFERENCES [rdr].[Event] ([EventId]) ON DELETE NO ACTION ON UPDATE NO ACTION;

