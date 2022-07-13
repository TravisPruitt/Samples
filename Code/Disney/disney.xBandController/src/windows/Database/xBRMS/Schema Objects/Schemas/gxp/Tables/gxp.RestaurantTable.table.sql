CREATE TABLE [gxp].[RestaurantTable] (
    [RestaurantTableId] INT           IDENTITY (1, 1) NOT NULL,
    [SourceTableId]     INT           NULL,
    [SourceTableName]   NVARCHAR (50) NULL,
    [FacilityId]        INT           NOT NULL,
    PRIMARY KEY CLUSTERED ([RestaurantTableId] ASC) WITH (ALLOW_PAGE_LOCKS = ON, ALLOW_ROW_LOCKS = ON, PAD_INDEX = OFF, IGNORE_DUP_KEY = OFF, STATISTICS_NORECOMPUTE = OFF)
);

