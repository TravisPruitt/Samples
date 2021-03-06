USE [Simulator]
GO
/****** Object:  Table [sim].[MagicBand]    Script Date: 12/04/2011 16:56:39 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [sim].[MagicBand](
	[MagicBandID] [bigint] NOT NULL,
	[BandID] [nvarchar](50) NOT NULL,
	[TapID] [nvarchar](50) NOT NULL,
	[LongRangeID] [nvarchar](50) NOT NULL,
	[GuestID] [bigint] NOT NULL,
	[PacketSequence] [int] NOT NULL,
	[NextTransmit] [datetime] NOT NULL,
 CONSTRAINT [PK_MagicBand] PRIMARY KEY CLUSTERED 
(
	[MagicBandID] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY],
 CONSTRAINT [AK_MagicBand_LongRangeID] UNIQUE NONCLUSTERED 
(
	[LongRangeID] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY],
 CONSTRAINT [AK_MagicBand_TapID] UNIQUE NONCLUSTERED 
(
	[TapID] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
CREATE NONCLUSTERED INDEX [IX_MagicBand_GuestID_NextTransmit] ON [sim].[MagicBand] 
(
	[GuestID] ASC,
	[NextTransmit] ASC
)
INCLUDE ( [TapID],
[LongRangeID],
[PacketSequence]) WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, SORT_IN_TEMPDB = OFF, IGNORE_DUP_KEY = OFF, DROP_EXISTING = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
GO
CREATE NONCLUSTERED INDEX [IX_MagicBand_NextTransmit] ON [sim].[MagicBand] 
(
	[NextTransmit] ASC
)
INCLUDE ( [TapID],
[LongRangeID],
[GuestID],
[PacketSequence]) WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, SORT_IN_TEMPDB = OFF, IGNORE_DUP_KEY = OFF, DROP_EXISTING = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
GO
ALTER TABLE [sim].[MagicBand]  WITH CHECK ADD  CONSTRAINT [FK_MagicBand_MagicBand] FOREIGN KEY([GuestID])
REFERENCES [sim].[Guest] ([GuestID])
GO
ALTER TABLE [sim].[MagicBand] CHECK CONSTRAINT [FK_MagicBand_MagicBand]
GO
