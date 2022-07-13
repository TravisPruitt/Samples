CREATE TABLE [gxp].[RestaurantEvent] (
    [RestaurantEventId]       INT           NOT NULL,
    [FacilityId]              INT           NOT NULL,
    [Source]                  NVARCHAR (50) NULL,
    [OpeningTime]             NVARCHAR (50) NULL,
    [ClosingTime]             NVARCHAR (50) NULL,
    [TableOccupancyAvailable] INT           NULL,
    [TableOccupancyOccupied]  INT           NULL,
    [TableOccupancyDirty]     INT           NULL,
    [TableOccupancyClosed]    INT           NULL,
    [SeatOccupancyTotalSeats] INT           NULL,
    [SeatOccupancyOccupied]   INT           NULL
);

