ALTER TABLE [gxp].[OrderEvent]
    ADD CONSTRAINT [FK__OrderEven__Order__58671BC9] FOREIGN KEY ([OrderId]) REFERENCES [gxp].[RestaurantOrder] ([OrderId]) ON DELETE NO ACTION ON UPDATE NO ACTION;

