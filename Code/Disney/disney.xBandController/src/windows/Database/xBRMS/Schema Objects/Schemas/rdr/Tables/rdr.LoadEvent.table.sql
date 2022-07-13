CREATE TABLE [rdr].[LoadEvent] (
    [EventId]   BIGINT        NOT NULL,
    [WaitTime]  INT           NOT NULL,
    [MergeTime] INT           NOT NULL,
    [CarID]     NVARCHAR (64) NULL
);

