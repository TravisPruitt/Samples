CREATE TABLE [rdr].[ExitEvent] (
    [EventId]   BIGINT        NOT NULL,
    [WaitTime]  INT           NOT NULL,
    [MergeTime] INT           NOT NULL,
    [TotalTime] INT           NOT NULL,
    [CarID]     NVARCHAR (64) NULL
);

