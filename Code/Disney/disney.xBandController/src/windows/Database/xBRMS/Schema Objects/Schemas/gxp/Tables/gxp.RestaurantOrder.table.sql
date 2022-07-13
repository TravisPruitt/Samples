CREATE TABLE [gxp].[RestaurantOrder] (
    [OrderId]     INT           IDENTITY (1, 1) NOT NULL,
    [FacilityId]  INT           NOT NULL,
    [OrderNumber] NVARCHAR (50) NULL,
    [OrderAmount] DECIMAL (18)  NULL,
    [PartySize]   INT           NULL,
    PRIMARY KEY CLUSTERED ([OrderId] ASC) WITH (ALLOW_PAGE_LOCKS = ON, ALLOW_ROW_LOCKS = ON, PAD_INDEX = OFF, IGNORE_DUP_KEY = OFF, STATISTICS_NORECOMPUTE = OFF)
);

