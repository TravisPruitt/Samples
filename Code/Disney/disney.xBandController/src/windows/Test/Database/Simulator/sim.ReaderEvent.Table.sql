USE [Simulator]
GO
/****** Object:  Table [sim].[ReaderEvent]    Script Date: 12/04/2011 16:56:40 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
SET ANSI_PADDING ON
GO
CREATE TABLE [sim].[ReaderEvent](
	[EventID] [bigint] IDENTITY(1,1) NOT NULL,
	[ReaderID] [int] NOT NULL,
	[EventNumber] [bigint] NOT NULL,
	[Timeval] [datetime] NOT NULL,
	[BandID] [varchar](16) NOT NULL,
	[SignalStrength] [int] NOT NULL,
	[Channel] [int] NOT NULL,
	[PacketSequence] [int] NOT NULL,
	[Frequency] [int] NOT NULL,
 CONSTRAINT [PK_ReaderEvent] PRIMARY KEY CLUSTERED 
(
	[EventID] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
SET ANSI_PADDING OFF
GO
CREATE NONCLUSTERED INDEX [IX_ReaderEvent_BandID] ON [sim].[ReaderEvent] 
(
	[BandID] ASC,
	[ReaderID] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, SORT_IN_TEMPDB = OFF, IGNORE_DUP_KEY = OFF, DROP_EXISTING = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
GO
CREATE NONCLUSTERED INDEX [IX_ReaderEvent_ReaderID] ON [sim].[ReaderEvent] 
(
	[ReaderID] ASC
)
INCLUDE ( [EventNumber]) WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, SORT_IN_TEMPDB = OFF, IGNORE_DUP_KEY = OFF, DROP_EXISTING = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
GO
ALTER TABLE [sim].[ReaderEvent]  WITH CHECK ADD  CONSTRAINT [FK_ReaderEvent_Reader] FOREIGN KEY([ReaderID])
REFERENCES [sim].[Reader] ([ReaderID])
GO
ALTER TABLE [sim].[ReaderEvent] CHECK CONSTRAINT [FK_ReaderEvent_Reader]
GO
