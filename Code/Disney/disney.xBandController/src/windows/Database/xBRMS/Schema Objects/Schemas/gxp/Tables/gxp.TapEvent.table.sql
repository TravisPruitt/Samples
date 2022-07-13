CREATE TABLE [gxp].[TapEvent] (
    [TapEventId]  INT           NOT NULL,
    [FacilityId]  INT           NOT NULL,
    [Source]      NVARCHAR (50) NULL,
    [SourceType]  NVARCHAR (50) NULL,
    [Terminal]    NVARCHAR (50) NULL,
    [OrderNumber] NVARCHAR (50) NULL,
    [Lane]        NVARCHAR (50) NULL,
    [IsBlueLaned] NVARCHAR (50) NULL,
    [PartySize]   INT           NULL
);

