USE [Simulator]
GO
/****** Object:  Table [sim].[Attraction]    Script Date: 12/04/2011 16:56:36 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [sim].[Attraction](
	[AttractionID] [int] IDENTITY(1,1) NOT NULL,
	[AttractionName] [nvarchar](50) NOT NULL,
	[MergeRatio] [decimal](18, 2) NOT NULL,
	[GuestsPerHour] [int] NOT NULL,
	[LoadAreaCapacity] [int] NOT NULL,
	[MinimumSecondsInQueue] [int] NOT NULL,
	[IsActive] [bit] NOT NULL,
 CONSTRAINT [PK_Attraction] PRIMARY KEY CLUSTERED 
(
	[AttractionID] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY],
 CONSTRAINT [AK_Attraction] UNIQUE NONCLUSTERED 
(
	[AttractionName] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
