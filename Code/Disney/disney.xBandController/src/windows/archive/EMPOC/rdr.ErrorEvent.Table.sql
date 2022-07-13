/****** Object:  Table [rdr].[ErrorEvent]    Script Date: 10/03/2011 14:08:33 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [rdr].[ErrorEvent](
	[EventId] [int] NOT NULL,
	[ErrorCode] [nvarchar](25) NOT NULL,
	[ErrorMessage] [nvarchar](250) NOT NULL,
 CONSTRAINT [PK_ErrorEvent] PRIMARY KEY CLUSTERED 
(
	[EventId] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]

GO
ALTER TABLE [rdr].[ErrorEvent]  WITH CHECK ADD  CONSTRAINT [FK_ErrorEvent_Event] FOREIGN KEY([EventId])
REFERENCES [rdr].[Event] ([EventId])
GO
ALTER TABLE [rdr].[ErrorEvent] CHECK CONSTRAINT [FK_ErrorEvent_Event]
GO


