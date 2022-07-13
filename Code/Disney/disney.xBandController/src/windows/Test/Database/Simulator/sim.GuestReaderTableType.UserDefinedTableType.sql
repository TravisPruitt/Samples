USE [Simulator]
GO
/****** Object:  UserDefinedTableType [sim].[GuestReaderTableType]    Script Date: 12/04/2011 16:56:38 ******/
CREATE TYPE [sim].[GuestReaderTableType] AS TABLE(
	[GuestID] [bigint] NULL,
	[EventTime] [datetime] NULL,
	[ReaderID] [int] NULL,
	[HasFastPassPlus] [bit] NULL,
	[SequenceNumber] [int] NULL
)
GO
