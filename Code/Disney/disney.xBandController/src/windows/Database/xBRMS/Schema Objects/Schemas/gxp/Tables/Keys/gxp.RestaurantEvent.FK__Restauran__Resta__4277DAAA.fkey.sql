ALTER TABLE [gxp].[RestaurantEvent]
    ADD CONSTRAINT [FK__Restauran__Resta__4277DAAA] FOREIGN KEY ([RestaurantEventId]) REFERENCES [gxp].[BusinessEvent] ([BusinessEventID]) ON DELETE NO ACTION ON UPDATE NO ACTION;

