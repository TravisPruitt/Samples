USE [Simulator]
GO
/****** Object:  Table [sim].[Guest]    Script Date: 12/04/2011 16:56:37 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [sim].[Guest](
	[GuestID] [bigint] NOT NULL,
	[LastName] [nvarchar](200) NOT NULL,
	[FirstName] [nvarchar](200) NOT NULL,
 CONSTRAINT [PK_Guest] PRIMARY KEY CLUSTERED 
(
	[GuestID] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
