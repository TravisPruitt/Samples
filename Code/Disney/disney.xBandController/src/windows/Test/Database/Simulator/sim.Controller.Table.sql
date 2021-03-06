USE [Simulator]
GO
/****** Object:  Table [sim].[Controller]    Script Date: 12/04/2011 16:56:38 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [sim].[Controller](
	[ControllerID] [int] NOT NULL,
	[ControllerURL] [nvarchar](256) NOT NULL,
	[ControllerName] [nvarchar](50) NOT NULL,
	[xViewLocationID] [int] NOT NULL,
 CONSTRAINT [PK_Controller] PRIMARY KEY CLUSTERED 
(
	[ControllerID] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY],
 CONSTRAINT [AK_Controller] UNIQUE NONCLUSTERED 
(
	[ControllerURL] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
ALTER TABLE [sim].[Controller]  WITH CHECK ADD  CONSTRAINT [FK_Controller_Attraction] FOREIGN KEY([ControllerID])
REFERENCES [sim].[Attraction] ([AttractionID])
GO
ALTER TABLE [sim].[Controller] CHECK CONSTRAINT [FK_Controller_Attraction]
GO
ALTER TABLE [sim].[Controller]  WITH CHECK ADD  CONSTRAINT [FK_Controller_xViewLocation] FOREIGN KEY([xViewLocationID])
REFERENCES [sim].[xViewLocation] ([xViewLocationID])
GO
ALTER TABLE [sim].[Controller] CHECK CONSTRAINT [FK_Controller_xViewLocation]
GO
