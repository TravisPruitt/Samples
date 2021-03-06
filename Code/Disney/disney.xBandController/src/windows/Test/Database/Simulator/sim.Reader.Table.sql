USE [Simulator]
GO
/****** Object:  Table [sim].[Reader]    Script Date: 12/04/2011 16:56:40 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [sim].[Reader](
	[ReaderID] [int] IDENTITY(1,1) NOT NULL,
	[ReaderName] [nvarchar](50) NOT NULL,
	[ControllerID] [int] NOT NULL,
	[Active] [bit] NOT NULL,
	[ReaderTypeID] [int] NOT NULL,
	[ReaderLocationTypeID] [int] NOT NULL,
	[Webport] [int] NOT NULL,
	[UpstreamUrl] [nvarchar](256) NOT NULL,
	[EventsInterval] [int] NOT NULL,
	[MaximumEvents] [int] NOT NULL,
	[LastUpstreamEvent] [bigint] NOT NULL,
 CONSTRAINT [PK_Reader] PRIMARY KEY CLUSTERED 
(
	[ReaderID] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
ALTER TABLE [sim].[Reader]  WITH CHECK ADD  CONSTRAINT [FK_Reader_Controller] FOREIGN KEY([ControllerID])
REFERENCES [sim].[Controller] ([ControllerID])
GO
ALTER TABLE [sim].[Reader] CHECK CONSTRAINT [FK_Reader_Controller]
GO
ALTER TABLE [sim].[Reader]  WITH CHECK ADD  CONSTRAINT [FK_Reader_ReaderLocationType] FOREIGN KEY([ReaderLocationTypeID])
REFERENCES [sim].[ReaderLocationType] ([ReaderLocationTypeID])
GO
ALTER TABLE [sim].[Reader] CHECK CONSTRAINT [FK_Reader_ReaderLocationType]
GO
ALTER TABLE [sim].[Reader]  WITH CHECK ADD  CONSTRAINT [FK_Reader_ReaderType] FOREIGN KEY([ReaderTypeID])
REFERENCES [sim].[ReaderType] ([ReaderTypeID])
GO
ALTER TABLE [sim].[Reader] CHECK CONSTRAINT [FK_Reader_ReaderType]
GO
ALTER TABLE [sim].[Reader] ADD  CONSTRAINT [DF_Reader_ControllerID]  DEFAULT ((6)) FOR [ControllerID]
GO
ALTER TABLE [sim].[Reader] ADD  CONSTRAINT [DF_Reader_EventsInterval]  DEFAULT ((200)) FOR [EventsInterval]
GO
ALTER TABLE [sim].[Reader] ADD  CONSTRAINT [DF_Reader_MaximumEvents]  DEFAULT ((-1)) FOR [MaximumEvents]
GO
