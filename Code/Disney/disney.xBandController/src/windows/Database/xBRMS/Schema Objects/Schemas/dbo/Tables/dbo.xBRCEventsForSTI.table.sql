CREATE TABLE [dbo].[xBRCEventsForSTI] (
    [EventID]        BIGINT        NOT NULL,
    [GuestID]        INT           NOT NULL,
    [RideID]         INT           NULL,
    [xPass]          BIT           NULL,
    [AttractionID]   INT           NULL,
    [EventTypeID]    INT           NOT NULL,
    [ReaderLocation] VARCHAR (128) NULL,
    [Timestamp]      DATETIME      NULL,
    [WaitTime]       INT           NULL,
    [MergeTime]      INT           NULL,
    [EventTypeName]  VARCHAR (128) NULL
);

