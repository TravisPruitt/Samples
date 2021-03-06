/****** Object:  Table [rdr].[LoadEvent]    Script Date: 08/31/2011 14:16:48 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [rdr].[LoadEvent](
	[EventId] [int] NOT NULL,
	[WaitTime] [int] NOT NULL,
	[MergeTime] [int] NOT NULL,
	[CarID] [nvarchar](20) NOT NULL,
 CONSTRAINT [PK_LoadEvent] PRIMARY KEY CLUSTERED 
(
	[EventId] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
ALTER TABLE [rdr].[LoadEvent]  WITH CHECK ADD  CONSTRAINT [FK_LoadEvent_Event] FOREIGN KEY([EventId])
REFERENCES [rdr].[Event] ([EventId])
GO
ALTER TABLE [rdr].[LoadEvent] CHECK CONSTRAINT [FK_LoadEvent_Event]
GO
