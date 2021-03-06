USE [Simulator]
GO
/****** Object:  Table [sim].[AttractionArrivalRate]    Script Date: 12/04/2011 16:56:38 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [sim].[AttractionArrivalRate](
	[AttractionID] [int] NOT NULL,
	[Hour] [int] NOT NULL,
	[Minute] [int] NOT NULL,
	[StandBy] [int] NOT NULL,
	[FastPassPlus] [int] NOT NULL,
 CONSTRAINT [PK_AttractionArrivalRate_1] PRIMARY KEY CLUSTERED 
(
	[AttractionID] ASC,
	[Hour] ASC,
	[Minute] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
ALTER TABLE [sim].[AttractionArrivalRate]  WITH CHECK ADD  CONSTRAINT [FK_AttractionArrivalRate_Attraction] FOREIGN KEY([AttractionID])
REFERENCES [sim].[Attraction] ([AttractionID])
GO
ALTER TABLE [sim].[AttractionArrivalRate] CHECK CONSTRAINT [FK_AttractionArrivalRate_Attraction]
GO
ALTER TABLE [sim].[AttractionArrivalRate] ADD  CONSTRAINT [DF_AttractionArrivalRate_Hour]  DEFAULT ((0)) FOR [Hour]
GO
ALTER TABLE [sim].[AttractionArrivalRate] ADD  CONSTRAINT [DF_AttractionArrivalRate_Minute]  DEFAULT ((0)) FOR [Minute]
GO
