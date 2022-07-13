ALTER TABLE [gxp].[TapEvent]
    ADD CONSTRAINT [FK__TapEvent__TapEve__30592A6F] FOREIGN KEY ([TapEventId]) REFERENCES [gxp].[BusinessEvent] ([BusinessEventID]) ON DELETE NO ACTION ON UPDATE NO ACTION;

