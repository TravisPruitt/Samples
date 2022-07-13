CREATE TABLE [gxp].[OrderEvent] (
    [OrderEventId] INT           NOT NULL,
    [OrderId]      INT           NOT NULL,
    [Source]       NVARCHAR (50) NULL,
    [SourceType]   NVARCHAR (50) NULL,
    [Terminal]     NVARCHAR (50) NULL,
    [Timestamp]    NVARCHAR (50) NULL,
    [User]         NVARCHAR (50) NULL,
    [TableId]      INT           NULL
);

