ALTER TABLE [gxp].[GuestOrderMap]
    ADD CONSTRAINT [FK__GuestOrde__Order__67A95F59] FOREIGN KEY ([OrderId]) REFERENCES [gxp].[RestaurantOrder] ([OrderId]) ON DELETE NO ACTION ON UPDATE NO ACTION;

