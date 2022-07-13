CREATE TABLE [rdr].[Attraction] (
    [AttractionID]       INT            IDENTITY (1, 1) NOT NULL,
    [AttractionName]     NVARCHAR (200) NOT NULL,
    [AttractionStatus]   SMALLINT       NULL,
    [SBQueueCap]         INT            NULL,
    [XPQueueCap]         INT            NULL,
    [DisplayName]        NVARCHAR (100) NULL,
    [AttractionCapacity] INT            NULL
);

