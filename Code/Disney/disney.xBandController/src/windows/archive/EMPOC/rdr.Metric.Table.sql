/****** Object:  Table [rdr].[Metric]    Script Date: 08/31/2011 14:16:47 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [rdr].[Metric](
	[MetricID] [int] IDENTITY(1,1) NOT NULL,
	[AttractionID] [int] NOT NULL,
	[StartTime] [datetime] NOT NULL,
	[EndTime] [datetime] NOT NULL,
	[MetricTypeID] [int] NOT NULL,
	[Guests] [int] NOT NULL,
	[Abandonments] [int] NOT NULL,
	[WaitTime] [int] NOT NULL,
	[MergeTime] [int] NOT NULL,
	[TotalTime] [int] NOT NULL,
 CONSTRAINT [PK_Metric] PRIMARY KEY CLUSTERED 
(
	[MetricID] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
ALTER TABLE [rdr].[Metric]  WITH CHECK ADD  CONSTRAINT [FK_Metric_Attraction] FOREIGN KEY([AttractionID])
REFERENCES [rdr].[Attraction] ([AttractionID])
GO
ALTER TABLE [rdr].[Metric] CHECK CONSTRAINT [FK_Metric_Attraction]
GO
ALTER TABLE [rdr].[Metric]  WITH CHECK ADD  CONSTRAINT [FK_Metric_MetricType] FOREIGN KEY([MetricTypeID])
REFERENCES [rdr].[MetricType] ([MetricTypeID])
GO
ALTER TABLE [rdr].[Metric] CHECK CONSTRAINT [FK_Metric_MetricType]
GO
