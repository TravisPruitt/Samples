/****** Object:  Table [rdr].[Event]    Script Date: 08/31/2011 14:16:47 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [rdr].[Event](
	[EventId] [int] IDENTITY(1,1) NOT NULL,
	[GuestID] [nvarchar](16) NOT NULL,
	[xPass] [bit] NOT NULL,
	[AttractionID] [int] NOT NULL,
	[EventTypeID] [int] NOT NULL,
	[ReaderLocation] [nvarchar](50) NOT NULL,
	[Timestamp] [datetime] NOT NULL,
 CONSTRAINT [PK_Event_1] PRIMARY KEY CLUSTERED 
(
	[EventId] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
ALTER TABLE [rdr].[Event]  WITH CHECK ADD  CONSTRAINT [FK_Event_Attraction] FOREIGN KEY([AttractionID])
REFERENCES [rdr].[Attraction] ([AttractionID])
GO
ALTER TABLE [rdr].[Event] CHECK CONSTRAINT [FK_Event_Attraction]
GO
ALTER TABLE [rdr].[Event]  WITH CHECK ADD  CONSTRAINT [FK_Event_EventType] FOREIGN KEY([EventTypeID])
REFERENCES [rdr].[EventType] ([EventTypeID])
GO
ALTER TABLE [rdr].[Event] CHECK CONSTRAINT [FK_Event_EventType]
GO
ALTER TABLE [rdr].[Event] ADD  CONSTRAINT [DF_Event_xPass]  DEFAULT ((0)) FOR [xPass]
GO
