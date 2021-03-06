/****** Object:  Table [tst].[Configuration]    Script Date: 08/31/2011 14:16:43 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [tst].[Configuration](
	[ConfigurationID] [int] IDENTITY(1,1) NOT NULL,
	[AttractionID] [int] NOT NULL,
	[GuestCount] [int] NOT NULL,
	[QueueWait] [int] NOT NULL,
	[StartTime] [datetime] NOT NULL,
	[IsExecuting] [bit] NOT NULL,
	[LastUpdated] [datetime] NOT NULL,
 CONSTRAINT [PK_Configuration] PRIMARY KEY CLUSTERED 
(
	[ConfigurationID] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
