CREATE TABLE [gxp].[TableGuestOrderMap] (
    [TableGuestId]      INT IDENTITY (1, 1) NOT NULL,
    [RestaurantTableId] INT NOT NULL,
    [OrderId]           INT NOT NULL,
    [BusinessEventId]   INT NOT NULL
);

