CREATE TABLE [rdr].[Event] (
    [EventId]        BIGINT        IDENTITY (1, 1) NOT NULL,
    [GuestID]        BIGINT        NOT NULL,
    [RideNumber]     INT           NOT NULL,
    [xPass]          BIT           NOT NULL,
    [FacilityID]     INT           NOT NULL,
    [EventTypeID]    INT           NOT NULL,
    [ReaderLocation] NVARCHAR (50) NOT NULL,
    [Timestamp]      DATETIME      NOT NULL,
    [BandTypeID]     INT           DEFAULT ((1)) NOT NULL,
    [RawMessage]     XML           NULL,
	[CreatedDate]    DATETIME      NOT NULL CONSTRAINT [DF_Event_CreatedDate]  DEFAULT (getutcdate())
);

