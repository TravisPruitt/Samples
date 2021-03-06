/****** Object:  Table [tst].[Guest]    Script Date: 08/31/2011 14:16:47 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [tst].[Guest](
	[GuestID] [nvarchar](16) NOT NULL,
	[ConfigurationID] [int] NOT NULL,
	[StartTime] [datetime] NOT NULL,
	[xPass] [bit] NOT NULL,
	[EntryTime] [int] NOT NULL,
	[WaitTime] [int] NOT NULL,
	[MergeTime] [int] NOT NULL,
	[LoadTime] [int] NOT NULL,
	[TotalTime] [int] NOT NULL,
 CONSTRAINT [PK_Guest] PRIMARY KEY CLUSTERED 
(
	[GuestID] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
ALTER TABLE [tst].[Guest]  WITH CHECK ADD  CONSTRAINT [FK_Guest_Configuration] FOREIGN KEY([ConfigurationID])
REFERENCES [tst].[Configuration] ([ConfigurationID])
GO
ALTER TABLE [tst].[Guest] CHECK CONSTRAINT [FK_Guest_Configuration]
GO
