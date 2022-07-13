ALTER TABLE [gxp].[OrderEvent]
    ADD CONSTRAINT [FK__OrderEven__Table__595B4002] FOREIGN KEY ([TableId]) REFERENCES [gxp].[RestaurantTable] ([RestaurantTableId]) ON DELETE NO ACTION ON UPDATE NO ACTION;

