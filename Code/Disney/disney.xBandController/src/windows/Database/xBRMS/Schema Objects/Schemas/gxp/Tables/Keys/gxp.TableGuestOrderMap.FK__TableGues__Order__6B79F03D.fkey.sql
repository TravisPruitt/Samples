ALTER TABLE [gxp].[TableGuestOrderMap]
    ADD CONSTRAINT [FK__TableGues__Order__6B79F03D] FOREIGN KEY ([OrderId]) REFERENCES [gxp].[RestaurantOrder] ([OrderId]) ON DELETE NO ACTION ON UPDATE NO ACTION;

