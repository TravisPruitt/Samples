/****** Object:  Table [rdr].[Attraction]    Script Date: 08/31/2011 14:16:43 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [rdr].[Attraction](
	[AttractionID] [int] IDENTITY(1,1) NOT NULL,
	[AttractionName] [nvarchar](200) NOT NULL,
	[AttractionStatus] [smallint] NULL,
	[SBQueueCap] [int] NULL,
	[XPQueueCap] [int] NULL,
	[DisplayName] [nvarchar](100) NULL,
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
