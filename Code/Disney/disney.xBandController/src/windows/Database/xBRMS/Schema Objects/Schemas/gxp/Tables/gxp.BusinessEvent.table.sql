CREATE TABLE [gxp].[BusinessEvent] (
    [BusinessEventID]        INT              IDENTITY (1, 1) NOT NULL,
    [EventLocationID]        INT              NOT NULL,
    [BusinessEventTypeID]    INT              NOT NULL,
    [BusinessEventSubTypeID] INT              NOT NULL,
    [ReferenceID]            NVARCHAR (50)    NULL,
    [GuestID]                BIGINT           NOT NULL,
    [Timestamp]              DATETIME         NOT NULL,
    [CorrelationID]          UNIQUEIDENTIFIER NOT NULL,
    [StartTime]              DATETIME         NULL,
    [EndTime]                DATETIME         NULL,
    [LocationID]             BIGINT           NULL,
    [EntertainmentID]        BIGINT           NULL,
    [RawMessage]             XML              NULL,
    [CreatedDate]            DATETIME         NOT NULL
);



