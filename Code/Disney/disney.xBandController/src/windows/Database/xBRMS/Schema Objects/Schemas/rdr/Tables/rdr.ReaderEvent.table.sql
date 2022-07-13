CREATE TABLE [rdr].[ReaderEvent] (
    [EventId]              BIGINT         NOT NULL,
    [ReaderLocationID]     NVARCHAR (200) NOT NULL,
    [ReaderName]           NVARCHAR (200) NOT NULL,
    [ReaderID]             NVARCHAR (200) NOT NULL,
    [IsWearingPrimaryBand] BIT            NOT NULL,
	[Confidence]           INT            NOT NULL CONSTRAINT [DF_ReaderEvent_Confidence]  DEFAULT (0)
);

