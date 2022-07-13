ALTER TABLE [gxp].[TableGuestOrderMap]
    ADD CONSTRAINT [FK__TableGues__Resta__6A85CC04] FOREIGN KEY ([RestaurantTableId]) REFERENCES [gxp].[RestaurantTable] ([RestaurantTableId]) ON DELETE NO ACTION ON UPDATE NO ACTION;

