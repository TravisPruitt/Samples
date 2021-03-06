USE [Simulator]
GO
/****** Object:  Table [sim].[ReaderLocationType]    Script Date: 12/04/2011 16:56:37 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [sim].[ReaderLocationType](
	[ReaderLocationTypeID] [int] IDENTITY(1,1) NOT NULL,
	[ReaderLocationTypeName] [nvarchar](50) NOT NULL,
 CONSTRAINT [PK_ReaderLocationType] PRIMARY KEY CLUSTERED 
(
	[ReaderLocationTypeID] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY],
 CONSTRAINT [AK_ReaderLocationType] UNIQUE NONCLUSTERED 
(
	[ReaderLocationTypeName] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
