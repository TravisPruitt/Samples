CREATE TABLE [gxp].[TableEvent] (
    [TableEventId] INT           NOT NULL,
    [FacilityId]   INT           NOT NULL,
    [Source]       NVARCHAR (50) NULL,
    [Terminal]     NVARCHAR (50) NULL,
    [UserName]     NVARCHAR (50) NULL,
    [TableId]      INT           NOT NULL
);

