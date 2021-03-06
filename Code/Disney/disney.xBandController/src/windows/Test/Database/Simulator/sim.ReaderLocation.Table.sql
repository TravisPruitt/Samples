USE [Simulator]
GO
/****** Object:  Table [sim].[ReaderLocation]    Script Date: 12/04/2011 16:56:40 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [sim].[ReaderLocation](
	[ReaderLocationID] [int] IDENTITY(1,1) NOT NULL,
	[ControllerID] [int] NOT NULL,
	[ReaderLocationName] [nvarchar](50) NOT NULL,
	[ReaderLocationTypeID] [int] NOT NULL,
 CONSTRAINT [PK_ReaderLocation] PRIMARY KEY CLUSTERED 
(
	[ReaderLocationID] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
ALTER TABLE [sim].[ReaderLocation]  WITH CHECK ADD  CONSTRAINT [FK_ReaderLocation_Controller] FOREIGN KEY([ControllerID])
REFERENCES [sim].[Controller] ([ControllerID])
GO
ALTER TABLE [sim].[ReaderLocation] CHECK CONSTRAINT [FK_ReaderLocation_Controller]
GO
ALTER TABLE [sim].[ReaderLocation]  WITH CHECK ADD  CONSTRAINT [FK_ReaderLocation_ReaderLocationType] FOREIGN KEY([ReaderLocationTypeID])
REFERENCES [sim].[ReaderLocationType] ([ReaderLocationTypeID])
GO
ALTER TABLE [sim].[ReaderLocation] CHECK CONSTRAINT [FK_ReaderLocation_ReaderLocationType]
GO
