USE [Simulator]
GO
/****** Object:  Table [sim].[GuestQueue]    Script Date: 12/04/2011 16:56:38 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [sim].[GuestQueue](
	[GuestID] [bigint] NOT NULL,
	[AttractionID] [int] NOT NULL,
	[ScheduledEntryTime] [datetime] NOT NULL,
	[HasFastPassPlus] [bit] NOT NULL,
	[GuestState] [int] NOT NULL,
	[EntryTime] [datetime] NULL,
	[MergeTime] [datetime] NULL,
	[LoadTime] [datetime] NULL,
	[ExitTime] [datetime] NULL,
 CONSTRAINT [PK_GuestQueue] PRIMARY KEY CLUSTERED 
(
	[GuestID] ASC,
	[AttractionID] ASC,
	[ScheduledEntryTime] ASC,
	[HasFastPassPlus] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
CREATE NONCLUSTERED INDEX [IX_GuestQueue_AttractionID_GuestState] ON [sim].[GuestQueue] 
(
	[AttractionID] ASC,
	[GuestState] ASC
)
INCLUDE ( [GuestID],
[HasFastPassPlus],
[EntryTime]) WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, SORT_IN_TEMPDB = OFF, IGNORE_DUP_KEY = OFF, DROP_EXISTING = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
GO
CREATE NONCLUSTERED INDEX [IX_GuestQueue1] ON [sim].[GuestQueue] 
(
	[AttractionID] ASC,
	[HasFastPassPlus] ASC,
	[GuestState] ASC
)
INCLUDE ( [GuestID],
[ScheduledEntryTime]) WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, SORT_IN_TEMPDB = OFF, IGNORE_DUP_KEY = OFF, DROP_EXISTING = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
GO
ALTER TABLE [sim].[GuestQueue]  WITH CHECK ADD  CONSTRAINT [FK_GuestPool_Attraction] FOREIGN KEY([AttractionID])
REFERENCES [sim].[Attraction] ([AttractionID])
GO
ALTER TABLE [sim].[GuestQueue] CHECK CONSTRAINT [FK_GuestPool_Attraction]
GO
ALTER TABLE [sim].[GuestQueue]  WITH CHECK ADD  CONSTRAINT [FK_GuestQueue_Guest] FOREIGN KEY([GuestID])
REFERENCES [sim].[Guest] ([GuestID])
GO
ALTER TABLE [sim].[GuestQueue] CHECK CONSTRAINT [FK_GuestQueue_Guest]
GO
