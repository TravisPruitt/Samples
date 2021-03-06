/****** Object:  Table [rdr].[MetricType]    Script Date: 08/31/2011 14:16:43 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [rdr].[MetricType](
	[MetricTypeID] [int] IDENTITY(1,1) NOT NULL,
	[MetricTypeName] [nvarchar](50) NOT NULL,
 CONSTRAINT [PK_MetricType] PRIMARY KEY CLUSTERED 
(
	[MetricTypeID] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
CREATE UNIQUE NONCLUSTERED INDEX [AK_MetricType] ON [rdr].[MetricType] 
(
	[MetricTypeName] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, SORT_IN_TEMPDB = OFF, IGNORE_DUP_KEY = OFF, DROP_EXISTING = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
GO
