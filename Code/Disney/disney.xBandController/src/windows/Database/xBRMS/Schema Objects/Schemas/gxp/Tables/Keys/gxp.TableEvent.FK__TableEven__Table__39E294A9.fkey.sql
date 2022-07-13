ALTER TABLE [gxp].[TableEvent]
    ADD CONSTRAINT [FK__TableEven__Table__39E294A9] FOREIGN KEY ([TableId]) REFERENCES [gxp].[RestaurantTable] ([RestaurantTableId]) ON DELETE NO ACTION ON UPDATE NO ACTION;

